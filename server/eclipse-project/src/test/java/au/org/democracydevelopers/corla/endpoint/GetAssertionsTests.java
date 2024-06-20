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

import au.org.democracydevelopers.corla.endpoint.GetAssertions.*;
import au.org.democracydevelopers.corla.model.ContestType;
import au.org.democracydevelopers.corla.model.vote.IRVParsingException;
import au.org.democracydevelopers.corla.raire.requestToRaire.GenerateAssertionsRequest;
import au.org.democracydevelopers.corla.raire.requestToRaire.GetAssertionsRequest;
import au.org.democracydevelopers.corla.raire.responseFromRaire.RaireServiceErrors;
import au.org.democracydevelopers.corla.util.testUtils;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.Request;
import spark.Response;
import us.freeandfair.corla.endpoint.Endpoint;
import us.freeandfair.corla.json.SubmittedAuditCVR;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.persistence.Persistence;

import java.io.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test the GetAssertions endpoint, both CSV and JSON versions. The response is supposed to be a zip file containing
 * the assertions.
 * TODO This really isn't a completely comprehensive set of tests yet. We also need:
 * - API testing
 * - Testing for retrieving the data from the zip.
 * - Testing that the service throws appropriate exceptions if the raire service connection isn't set up properly.
 * See <a href="https://github.com/DemocracyDevelopers/colorado-rla/issues/125">...</a>
 */
public class GetAssertionsTests {

    private static final Logger LOGGER = LogManager.getLogger(GetAssertionsTests.class);

    private static final Gson GSON = new Gson();

    private static String boulderMayoral = "City of Boulder Mayoral Candidates";
    private static String tinyIRV = "TinyExample1";

    private Choice alice = new Choice("Alice", "", false, false);
    private Choice bob = new Choice("Bob", "", false, false);
    private Choice chuan = new Choice("Chuan", "", false, false);

    private List<Choice> boulderMayoralCandidates = List.of(
            new Choice("Aaron Brockett", "", false, false),
            new Choice("Nicole Speer", "", false, false),
            new Choice("Bob Yates", "", false, false),
            new Choice("Paul Tweedlie", "", false, false)
    );

    // IRV Contests for mocking the IRVContestCollector.
    private Contest tinyIRVExample = new Contest(tinyIRV, new County("Arapahoe", 3L), ContestType.IRV.toString(),
            List.of(alice, bob, chuan), 3, 1, 0);
    private ContestResult tinyIRVContestResult = new ContestResult(tinyIRV);
    private Contest boulderMayoralContest = new Contest(boulderMayoral, new County("Boulder", 7L), ContestType.IRV.toString(),
            boulderMayoralCandidates, 4, 1, 0);
    private ContestResult boulderIRVContestResult = new ContestResult(boulderMayoral);
    private List<ContestResult> mockedIRVContestResults = List.of(boulderIRVContestResult, tinyIRVContestResult);


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
    }


    /**
     * Calls the getAssertions main endpoint function, for csv, and checks that the right file names are present in the
     * zip.
     * @throws Exception never.
     */
    @Test
    public void rightFileNamesInZipCSV() throws Exception {
        testUtils.log(LOGGER, "rightFileNamesInZipCSV");

        try (MockedStatic<IRVContestCollector> mockIRVContestResults = Mockito.mockStatic(IRVContestCollector.class)) {
            mockIRVContestResults.when(IRVContestCollector::getIRVContestResults).thenReturn(mockedIRVContestResults);

            GetAssertions endpoint = new GetAssertions();
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(bytesOut);
            endpoint.getAssertions(zos, BigDecimal.valueOf(0.03), "http://localhost:8080/raire/get-assertions", "csv");

            byte[] bytes = bytesOut.toByteArray();
            InputStream bais = new ByteArrayInputStream(bytes);
            ZipInputStream in = new ZipInputStream(bais);
            ZipEntry firstEntry = in.getNextEntry();
            assertEquals(firstEntry.getName(), "CityofBoulderMayoralCandidates_assertions.csv");
            ZipEntry secondEntry = in.getNextEntry();
            assertEquals("TinyExample1" + "_assertions.csv", secondEntry.getName());
            ZipEntry thirdEntry = in.getNextEntry();
            assertNull(thirdEntry);
        }
    }

    /**
     * Calls the getAssertions main endpoint function, for json, and checks that the right file names are present in the
     * zip.
     * @throws Exception never.
     */
    @Test
    public void rightFileNamesInZipJSON() throws Exception {
        testUtils.log(LOGGER, "rightFileNamesInZipJSON");
        try (MockedStatic<IRVContestCollector> mockIRVContestResults = Mockito.mockStatic(IRVContestCollector.class)) {
            mockIRVContestResults.when(IRVContestCollector::getIRVContestResults).thenReturn(mockedIRVContestResults);

            GetAssertions endpoint = new GetAssertions();
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(bytesOut);
            endpoint.getAssertions(zos, BigDecimal.valueOf(0.03), "http://localhost:8080/raire/get-assertions", "json");

            byte[] bytes = bytesOut.toByteArray();
            InputStream bais = new ByteArrayInputStream(bytes);
            ZipInputStream in = new ZipInputStream(bais);
            ZipEntry firstEntry = in.getNextEntry();
            assertEquals(firstEntry.getName(), "CityofBoulderMayoralCandidates_assertions.json");
            ZipEntry secondEntry = in.getNextEntry();
            assertEquals("TinyExample1" + "_assertions.json", secondEntry.getName());
            ZipEntry thirdEntry = in.getNextEntry();
            assertNull(thirdEntry);
        }
    }


    @Test(expectedExceptions = RuntimeException.class)
    public void badURLThrowsRuntimeExceptionCSV() throws Exception {
        testUtils.log(LOGGER, "badURLThrowsRuntimeExceptionCSV");
        try (MockedStatic<IRVContestCollector> mockIRVContestResults = Mockito.mockStatic(IRVContestCollector.class)) {
            mockIRVContestResults.when(IRVContestCollector::getIRVContestResults).thenReturn(mockedIRVContestResults);

            GetAssertions endpoint = new GetAssertions();
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(bytesOut);
            endpoint.getAssertions(zos, BigDecimal.valueOf(0.03), "http://localhost:8080/bad-url", "csv");

            byte[] bytes = bytesOut.toByteArray();
            InputStream bais = new ByteArrayInputStream(bytes);
            ZipInputStream in = new ZipInputStream(bais);
            ZipEntry firstEntry = in.getNextEntry();
            assertEquals(firstEntry.getName(), "CityofBoulderMayoralCandidates_assertions.json");
            ZipEntry secondEntry = in.getNextEntry();
            assertEquals("TinyExample1" + "_assertions.json", secondEntry.getName());
            ZipEntry thirdEntry = in.getNextEntry();
            assertNull(thirdEntry);
        }
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void badURLThrowsRuntimeExceptionJSON() throws Exception {
        testUtils.log(LOGGER, "badURLThrowsRuntimeExceptionJSON");
        try (MockedStatic<IRVContestCollector> mockIRVContestResults = Mockito.mockStatic(IRVContestCollector.class)) {
          mockIRVContestResults.when(IRVContestCollector::getIRVContestResults).thenReturn(mockedIRVContestResults);

          GetAssertions endpoint = new GetAssertions();
          ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
          ZipOutputStream zos = new ZipOutputStream(bytesOut);
          endpoint.getAssertions(zos, BigDecimal.valueOf(0.03), "http://localhost:8080/bad-url", "json");

          byte[] bytes = bytesOut.toByteArray();
          InputStream bais = new ByteArrayInputStream(bytes);
          ZipInputStream in = new ZipInputStream(bais);
          ZipEntry firstEntry = in.getNextEntry();
          assertEquals(firstEntry.getName(), "CityofBoulderMayoralCandidates_assertions.json");
          ZipEntry secondEntry = in.getNextEntry();
          assertEquals("TinyExample1" + "_assertions.json", secondEntry.getName());
          ZipEntry thirdEntry = in.getNextEntry();
          assertNull(thirdEntry);
        }
    }
}