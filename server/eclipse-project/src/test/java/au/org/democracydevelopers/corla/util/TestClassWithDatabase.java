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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import us.freeandfair.corla.persistence.Persistence;

/**
 * This class is designed to be extended by any test class that needs to interact with a test
 * instantiation of the colorado-rla database. It provides convenience methods for instantiating
 * a postgres container (initialised with one a given SQL script) and hibernate properties.
 */
public abstract class TestClassWithDatabase {

  /**
   * Blank properties for submitting to the DominionCVRExportParser instance.
   */
  protected static final Properties blank = new Properties();

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
   */
  public static PostgreSQLContainer<?> createTestContainer() {
    return new PostgreSQLContainer<>("postgres:15-alpine")
        // None of these actually have to be the same as the real database (except its name),
        // but this makes it easy to match the setup scripts.
        .withDatabaseName("corla")
        .withUsername("corlaadmin")
        .withPassword("corlasecret")
        .withInitScript("SQL/corla.sql");
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

}
