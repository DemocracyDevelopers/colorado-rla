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

import au.org.democracydevelopers.corla.raire.requestToRaire.GetAssertionsRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.freeandfair.corla.endpoint.Endpoint;
import us.freeandfair.corla.json.SubmittedAuditCVR;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.persistence.Persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Properties;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * Test the GetAssertions endpoint, both CSV and JSON versions. The response is supposed to be a zip file containing
 * the assertions.
 */
public class GetAssertionsTests {

  private static final Logger LOGGER = LogManager.getLogger(GetAssertionsTests.class);

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
   * Mocked CVRContestInfo representing the vote "A", "B", "C", "D".
   */
  @Mock
  private CVRContestInfo ABCD;

  /**
   * Mocked CVRContestInfo representing the vote "B", "A", "C", "D".
   */
  @Mock
  private CVRContestInfo BACD;

  /**
   * Mocked CVRContestInfo representing the vote "A".
   */
  @Mock
  private CVRContestInfo A;

  /**
   * Mocked CVRContestInfo representing the vote "B".
   */
  @Mock
  private CVRContestInfo B;

  /**
   * Mocked CastVoteRecord to represent a CVR.
   */
  @Mock
  private CastVoteRecord cvr;

  /**
   * Mocked CastVoteRecord to represent an audited CVR.
   */
  @Mock
  private CastVoteRecord auditedCvr;

  @Mock
  private GetAssertions endpoint;

  /**
   * Initialise mocked objects prior to the first test.
   */
  @BeforeClass
  public void initMocks() {
    MockitoAnnotations.openMocks(this);

    when(endpoint.requiredAuthorization()).thenReturn(Endpoint.AuthorizationType.NONE);

    when(ABCD.choices()).thenReturn(List.of("A", "B", "C", "D"));
    when(BACD.choices()).thenReturn(List.of("B", "A", "C", "D"));
    // when(blank.choices()).thenReturn(List.of());
    when(A.choices()).thenReturn(List.of("A"));
    when(B.choices()).thenReturn(List.of("B"));

    when(cvr.id()).thenReturn(1L);
  }




  @Test
  public void test2() throws Exception {
    GetAssertionsRequest request = new GetAssertionsRequest("testContest",1000,
            List.of("Alice","Bob"), "Alice", BigDecimal.valueOf(0.03));
    var client = HttpClientBuilder.create().build();

    // Send an Audit cvr
    HttpUriRequest getAssertionsRequest  = RequestBuilder.create("GET")
            // Note: use port 8888 for things requiring login.
            .setUri("http://localhost:3000/get-assertions")
            // .setEntity(new StringEntity(request.toString(), ContentType.APPLICATION_JSON))
            .build();
    HttpResponse getAssertionsResponse = client.execute(getAssertionsRequest);
    // HttpResponse response = client.execute(request);

    assertEquals(getAssertionsResponse.getEntity().getContent().toString(),"Test Error");
    assertEquals(getAssertionsResponse.getStatusLine().getStatusCode(), 500);
  }
}