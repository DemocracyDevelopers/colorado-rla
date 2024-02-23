package au.org.democracydevelopers.endpoint;

import java.math.BigDecimal;
import java.time.Instant;

import au.org.democracydevelopers.endpoint.GenerateAssertions;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import us.freeandfair.corla.model.AuditInfo;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.Setup;

public class GenerateAssertionsTest {

  @BeforeTest()
  public void setUp() {
    Setup.setProperties();
    Persistence.beginTransaction();

    // Create DoSDashboard with some audit information.
    DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
    dosdb.updateAuditInfo(new AuditInfo("general", Instant.now(), Instant.now(),
        "12856782643571354365", BigDecimal.valueOf(5)));
    Persistence.saveOrUpdate(dosdb);
    Persistence.flush();
  }

//  @AfterTest()
//  public void tearDown() {
//    try {
//      Persistence.rollbackTransaction();
//    } catch (Exception e) {
//    }
//  }

  @Test()
  public void testAssertionGenerationEndpointWithTrivialExample() {
    GenerateAssertions generateAssertions = new GenerateAssertions();
    generateAssertions.endpointBody(null, null);
  }

}
