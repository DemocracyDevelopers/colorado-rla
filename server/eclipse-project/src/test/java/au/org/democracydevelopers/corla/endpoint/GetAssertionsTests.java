/*
Democracy Developers IRV extensions to colorado-rla.

@copyright 2024 Colorado Department of State

These IRV extensions are designed to connect to a running instance of the raire
service (https://github.com/DemocracyDevelopers/raire-service), in order to
generate assertions that can be audited using colorado-rla.

The colorado-rla IRV extensions are free software: you can redistribute it and/or modify it under the terms
of the GNU Affero General Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

The colorado-rla IRV extensions are distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with
raire-service. If not, see <https://www.gnu.org/licenses/>.
*/

package au.org.democracydevelopers.corla.endpoint;

import au.org.democracydevelopers.corla.model.ContestType;
import au.org.democracydevelopers.corla.util.testUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.HttpStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import us.freeandfair.corla.model.*;

import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.testng.Assert.assertEquals;

/**
 * Test the GetAssertions endpoint, both CSV and JSON versions. The response is supposed to be a zip file containing
 * the assertions.
 * TODO This really isn't a completely comprehensive set of tests yet. We also need:
 * - API testing
 * - Testing for retrieving the data from the zip.
 * - More comprehensive testing of filename sanitization (from contest names).
 * - Testing that the service throws appropriate exceptions if the raire service connection isn't set up properly.
 * See <a href="https://github.com/DemocracyDevelopers/colorado-rla/issues/125">...</a>
 */
public class GetAssertionsTests {

    private static final Logger LOGGER = LogManager.getLogger(GetAssertionsTests.class);

    /**
     * Two IRV contests for mocking the IRVContestCollector: Boulder Mayoral '23 and a tiny constructed example.
     */
    private final static String boulderMayoral = "City of Boulder Mayoral Candidates";
    private final static String tinyIRV = "TinyExample1";

    private final Choice alice = new Choice("Alice", "", false, false);
    private final Choice bob = new Choice("Bob", "", false, false);
    private final Choice chuan = new Choice("Chuan", "", false, false);

    private final List<Choice> boulderMayoralCandidates = List.of(
            new Choice("Aaron Brockett", "", false, false),
            new Choice("Nicole Speer", "", false, false),
            new Choice("Bob Yates", "", false, false),
            new Choice("Paul Tweedlie", "", false, false)
    );

    private final Contest tinyIRVExample = new Contest(tinyIRV, new County("Arapahoe", 3L), ContestType.IRV.toString(),
            List.of(alice, bob, chuan), 3, 1, 0);
    private final ContestResult tinyIRVContestResult = new ContestResult(tinyIRV);
    private final Contest boulderMayoralContest = new Contest(boulderMayoral, new County("Boulder", 7L), ContestType.IRV.toString(),
            boulderMayoralCandidates, 4, 1, 0);
    private final ContestResult boulderIRVContestResult = new ContestResult(boulderMayoral);
    private final List<ContestResult> mockedIRVContestResults = List.of(boulderIRVContestResult, tinyIRVContestResult);

    /**
     * Endpoint for getting assertions.
     */
    final String getAssertionsEndpoint = "/raire/get-assertions";

    /**
     * JSON suffix.
     */
    final String json = "json";

    /**
     * CSV suffix.
     */
    final String csv = "csv";

    /**
     * Wiremock server for mocking the raire service.
     * (Note the default of 8080 clashes with the raire-service default, so this is different.)
     */
    final WireMockServer wireMockRaireServer = new WireMockServer(8111);

    /**
     * Base url - this is set up to use the wiremock server, but could be set here to wherever you have the
     * raire-service running to test with that directly.
     */
    static String baseUrl;

    /**
     * Initialise mocked objects prior to the first test.
     */
    @BeforeClass
    public void initMocks() {
        MockitoAnnotations.openMocks(this);

        boulderIRVContestResult.setAuditReason(AuditReason.COUNTY_WIDE_CONTEST);
        boulderIRVContestResult.setBallotCount(100000L);
        boulderIRVContestResult.setWinners(Set.of("Aaron Brockett"));
        boulderIRVContestResult.addContests(Set.of(boulderMayoralContest));

        tinyIRVContestResult.setAuditReason(AuditReason.COUNTY_WIDE_CONTEST);
        tinyIRVContestResult.setBallotCount(10L);
        tinyIRVContestResult.setWinners(Set.of("Alice"));
        tinyIRVContestResult.addContests(Set.of(tinyIRVExample));

        wireMockRaireServer.start();
        baseUrl = wireMockRaireServer.baseUrl();
        configureFor("localhost", wireMockRaireServer.port());
        stubFor(post(urlEqualTo(getAssertionsEndpoint + "-" + csv))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/octet-stream")
                        .withBody("Test csv")));
        stubFor(post(urlEqualTo(getAssertionsEndpoint + "-" + json))
                .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader("Content-Type", "application/json")
                .withBody("Test json")));

    }

    @AfterClass
    public void closeMocks() {
        wireMockRaireServer.stop();
    }


    /**
     * Test data for the two valid IRV contests, with json and csv.  This gives the successfully-sanitized version of
     * the contest names.
     */
    @DataProvider(name = "TwoIRVContests")
    public static String[][] TwoIRVContests() {
        return new String[][]{
                {"CityofBoulderMayoralCandidates", tinyIRV, "json"},
                {"CityofBoulderMayoralCandidates", tinyIRV, "csv"}
        };
    }

    /**
     * Calls the getAssertions main endpoint function and checks that the right file names are present in the
     * zip.
     * @throws Exception never.
     */
    @Test(dataProvider = "TwoIRVContests")
    public void rightFileNamesInZip(String contestName1, String contestName2, String suffix) throws Exception {
        testUtils.log(LOGGER, "rightFileNamesInZip");
        try (MockedStatic<IRVContestCollector> mockIRVContestResults = Mockito.mockStatic(IRVContestCollector.class)) {
            mockIRVContestResults.when(IRVContestCollector::getIRVContestResults).thenReturn(mockedIRVContestResults);

            GetAssertions endpoint = new GetAssertions();
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(bytesOut);
            endpoint.getAssertions(zos, BigDecimal.valueOf(0.03), baseUrl + getAssertionsEndpoint, suffix);

            byte[] bytes = bytesOut.toByteArray();
            InputStream bais = new ByteArrayInputStream(bytes);
            ZipInputStream in = new ZipInputStream(bais);
            ZipEntry firstEntry = in.getNextEntry();
            assertNotNull(firstEntry);
            assertEquals(firstEntry.getName(), contestName1 + "_assertions." + suffix);
            ZipEntry secondEntry = in.getNextEntry();
            assertNotNull(secondEntry);
            assertEquals(secondEntry.getName(), contestName2 + "_assertions." + suffix);
            ZipEntry thirdEntry = in.getNextEntry();
            assertNull(thirdEntry);
        }
    }

    /**
     * Good urls with bad endpoints.
     */
    @DataProvider(name = "SampleBadEndpoints")
    public static String[][] SampleBadEndpoints() {
        return new String[][]{
                {baseUrl + "/badUrl", "csv"},
                {baseUrl + "/badUrl", "json"}
        };
    }

    /**
     * Checks that, when given a bad endpoint, a runtime exception is thrown, for both csv and json.
     * @throws Exception always.
     */
    @Test(dataProvider ="SampleBadEndpoints", expectedExceptions = RuntimeException.class)
    public void badEndpointThrowsRuntimeException(String url, String suffix) throws Exception {
        testUtils.log(LOGGER, "badEndpointThrowsRuntimeException");
        try (MockedStatic<IRVContestCollector> mockIRVContestResults
                     = Mockito.mockStatic(IRVContestCollector.class)) {
            mockIRVContestResults.when(IRVContestCollector::getIRVContestResults)
                    .thenReturn(mockedIRVContestResults);

            GetAssertions endpoint = new GetAssertions();
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(bytesOut);
            endpoint.getAssertions(zos, BigDecimal.valueOf(0.03), url, suffix);
        }
    }

    /**
     * Bad urls.
     */
    @DataProvider(name = "SampleBadUrls")
    public static String[][] SampleBadUrls() {
        return new String[][]{
                {"completelyNotAUrl" + "/badUrl", "csv"},
                {"completelyNotAUrl" + "/badUrl", "json"}
        };
    }

    /**
     * Checks that, when given a bad url, an appropriate url-parsing exception is thrown, for both csv and json.
     * @throws Exception always.
     */
    @Test(dataProvider ="SampleBadUrls", expectedExceptions = {MalformedURLException.class, URISyntaxException.class})
    public void badUrlThrowsUrlException(String url, String suffix) throws Exception {
        testUtils.log(LOGGER, "badUrlThrowsUrlException");
        try (MockedStatic<IRVContestCollector> mockIRVContestResults
                     = Mockito.mockStatic(IRVContestCollector.class)) {
            mockIRVContestResults.when(IRVContestCollector::getIRVContestResults)
                    .thenReturn(mockedIRVContestResults);

            GetAssertions endpoint = new GetAssertions();
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(bytesOut);
            endpoint.getAssertions(zos, BigDecimal.valueOf(0.03), url, suffix);
        }
    }
}