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
import au.org.democracydevelopers.corla.raire.requestToRaire.GenerateAssertionsRequest;
import au.org.democracydevelopers.corla.raire.requestToRaire.GetAssertionsRequest;
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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * Test the GetAssertions endpoint, both CSV and JSON versions. The response is supposed to be a zip file containing
 * the assertions.
 */
public class GetAssertionsTests {

  private static final Logger LOGGER = LogManager.getLogger(GetAssertionsTests.class);

  private static final Gson GSON = new Gson();

  private static String boulderMayoral = "City of Boulder Mayoral Candidates";

  private Choice alice = new Choice("Alice", "", false, false);
  private Choice bob = new Choice("Bob", "", false, false);

  private Contest testContest = new Contest("testContest", new County("testCounty", 1L), ContestType.IRV.toString(),
          List.of(alice, bob), 2, 1, 1);

  private List<Choice> boulderMayoralCandidates = List.of(
          new Choice("Aaron Brockett", "", false, false),
          new Choice("Nicole Speer", "", false, false),
          new Choice("Bob Yates", "", false, false),
          new Choice("Paul Tweedlie", "", false, false)
  );

  private Contest boulderMayoralContest = new Contest(boulderMayoral, new County("Boulder", 7L), ContestType.IRV.toString(),
          boulderMayoralCandidates, 4, 1, 0);
  private ContestResult boulderIRVContestResult = new ContestResult(boulderMayoral);
  private List<ContestResult> mockedIRVContestResults = List.of(boulderIRVContestResult);

  /**
   * Container for the mock-up database.
   */
  static PostgreSQLContainer<?> postgres
          = new PostgreSQLContainer<>("postgres:15-alpine")
          // None of these actually have to be the same as the real database (except its name), but this
          // makes it easy to match the setup scripts.
          .withDatabaseName("corla")
          .withUsername("corlaadmin")
          .withPassword("corlasecret")
          // .withInitScripts("corlaInit.sql","contest.sql");
          .withInitScript("SQL/corlaInit.sql");

  @BeforeClass
  public static void beforeAll() {
    postgres.start();
    Properties hibernateProperties = new Properties();
    hibernateProperties.setProperty("hibernate.driver", "org.postgresql.Driver");
    hibernateProperties.setProperty("hibernate.url", postgres.getJdbcUrl());
    hibernateProperties.setProperty("hibernate.user", postgres.getUsername());
    hibernateProperties.setProperty("hibernate.pass", postgres.getPassword());
    hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
    Persistence.setProperties(hibernateProperties);
    Persistence.beginTransaction();

  }

  @AfterClass
  public static void afterAll() {
    postgres.stop();
  }

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
  }




  @Test
  public void test2() throws Exception {

    try (MockedStatic<IRVContestCollector> mockIRVContestResults = Mockito.mockStatic(IRVContestCollector.class)) {
      mockIRVContestResults.when(IRVContestCollector::getIRVContestResults).thenReturn(mockedIRVContestResults);

    List<ContestResult> testMock = IRVContestCollector.getIRVContestResults();
    assertEquals(1, testMock.size());
    assertEquals(boulderMayoral, testMock.get(0).getContestName());

    GetAssertions endpoint = new GetAssertions();
    ZipOutputStream zos = new ZipOutputStream(new ByteArrayOutputStream());
    endpoint.getAssertions(zos, BigDecimal.valueOf(0.03),"http://localhost:8080/raire/get-assertions","csv");

    CloseableHttpClient client = HttpClientBuilder.create().build();
      GenerateAssertionsRequest generateAssertionsRequest = new GenerateAssertionsRequest(boulderMayoral,
              100000, 5, boulderMayoralCandidates.stream().map(Choice::name).toList());
      HttpPost generateAssertionsPost = new HttpPost("http://localhost:8080/raire/generate-assertions");
      generateAssertionsPost.addHeader("content-type", "application/json");
      generateAssertionsPost.setEntity(new StringEntity(GSON.toJson(generateAssertionsRequest)));

      HttpResponse generateAssertionsResponse = client.execute(generateAssertionsPost);

      ZipOutputStream zos2 = new ZipOutputStream(new ByteArrayOutputStream());
      endpoint.getAssertions(zos2, BigDecimal.valueOf(0.03),"http://localhost:8080/raire/get-assertions","csv");
    }
  }
}