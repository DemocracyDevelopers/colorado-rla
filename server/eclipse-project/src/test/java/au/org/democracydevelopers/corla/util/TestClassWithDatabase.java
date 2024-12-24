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

package au.org.democracydevelopers.corla.util;

import java.util.Properties;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import us.freeandfair.corla.persistence.Persistence;

import static au.org.democracydevelopers.corla.util.PropertiesLoader.loadProperties;

/**
 * This class is designed to be extended by any test class that needs to interact with a test
 * instantiation of the colorado-rla database. It provides convenience methods for instantiating
 * a postgres container (initialised with one a given SQL script) and hibernate properties.
 * Database configuration properties are loaded from test.properties, except that the Hibernate URL
 * is overridden with the test container's URL.
 * Important gotcha: although you can extend this class and write a new @BeforeClass method, which
 * will be executed after any @BeforeClass methods in this class, you *must* give the method a new
 * name - otherwise it seems to silently not run.
 */
public abstract class TestClassWithDatabase {

  /**
   * Blank properties for submitting to the DominionCVRExportParser instance.
   */
  protected static final Properties blank = new Properties();

  /**
   * Properties derived from test.properties.
   */
  protected static Properties config = loadProperties();

  /**
   * The string used to identify the configured port in test.properties.
   */
  protected final static String generateAssertionsPortNumberString = "generate_assertions_mock_port";

  /**
   * The string used to identify the configured port in test.properties.
   */
  protected final static String getAssertionsPortNumberString = "get_assertions_mock_port";

  /**
   * Container for the mock-up database.
   */
  static PostgreSQLContainer<?> postgres = createTestContainer();


  @BeforeClass
  public static void beforeAll() {
    postgres.start();
    // Each class that inherits from TestClassWithDatabase gets a different url for the mocked DB.
    // Everything else is the same.
    config.setProperty("hibernate.url", postgres.getJdbcUrl());
    Persistence.setProperties(config);
  }

  @AfterClass
  public static void afterAll() {
    postgres.stop();
  }

  /**
   * Begin a new transaction before each test method in the class is run.
   */
  @BeforeMethod
  public static void beforeTest(){
    Persistence.beginTransaction();
  }

  /**
   * Rollback any changes to the (test) database after each test method is run.
   */
  @AfterMethod
  public static void afterTest(){
    try {
      Persistence.rollbackTransaction();
    } catch (Exception ignored) {
    }
  }

  /**
   * Create and return a postgres test container for the purposes of testing functionality that
   * interacts with the database.
   * @return a postgres test container representing a test database.
   * FIXME This is more general than Matt's edits - suggest retaining this version.
   */
  public static PostgreSQLContainer<?> createTestContainer() {
    return new PostgreSQLContainer<>("postgres:15-alpine")
        // None of these actually have to be the same as the real database (except its name),
        // but this makes it easy to match the setup scripts.
        .withDatabaseName("corla")
        .withUsername(config.getProperty("hibernate.user"))
        .withPassword(config.getProperty("hibernate.pass"))
        .withInitScript("SQL/corla.sql");
  }

  /**
   * Do the basic setup common to all the database test containers, which are all the same except for having a different url,
   * generated at init.
   * @param postgres the database container.
   * @return the database delegate.
   * FIXME possibly no longer needed, though actually the opportunity to configure the setup may be needed for
   * some DD tests.
   */
  protected static JdbcDatabaseDelegate setupContainerStartPostgres(PostgreSQLContainer<?> postgres) {
    postgres.start();

    // Each class that inherits from TestClassWithDatabase gets a different url for the mocked DB.
    // Everything else is the same.
    config.setProperty("hibernate.url", postgres.getJdbcUrl());
    Persistence.setProperties(config);

    return new JdbcDatabaseDelegate(postgres, "");
  }

  /**
   * Create and return a hibernate properties object for use in testing functionality that
   * interacts with the database.
   * @param postgres Postgres test container representing a test version of the database.
   * @return Hibernate persistence properties.
   */
  public static Properties createHibernateProperties(PostgreSQLContainer<?> postgres) {
    Properties hibernateProperties = new Properties();
    hibernateProperties.setProperty("hibernate.driver", "org.postgresql.Driver");
    hibernateProperties.setProperty("hibernate.url", postgres.getJdbcUrl());
    hibernateProperties.setProperty("hibernate.user", postgres.getUsername());
    hibernateProperties.setProperty("hibernate.pass", postgres.getPassword());
    hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");

    return hibernateProperties;
  }

  protected static void runSQLSetupScript(String initScriptPath) {
    var containerDelegate = new JdbcDatabaseDelegate(postgres, "");
    ScriptUtils.runInitScript(containerDelegate, initScriptPath);
  }
}
