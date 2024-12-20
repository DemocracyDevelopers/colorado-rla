package us.freeandfair.corla.model;

import java.io.IOException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.Map;
import java.util.OptionalInt;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.testng.annotations.*;
import static org.testng.Assert.*;
import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableHashCode;

import us.freeandfair.corla.csv.ContestNameParser;
import us.freeandfair.corla.json.VersionExclusionStrategy;
import us.freeandfair.corla.util.TestClassWithDatabase;

public class AuditInfoTest extends TestClassWithDatabase {
  public Gson gson;

  @BeforeClass
  public void AuditInfoTest() {
    gson = new GsonBuilder().setPrettyPrinting().create();
  }

  @Test
  public void testCanonicalContests() {
    Map<String, Set<String>> contestMap = new TreeMap<String, Set<String>>();

    Set<String> contests = new HashSet<String>();
    contests.add("Hooligan Race");
    contests.add("Pole Paddle Pedal");
    contestMap.put("Chaffee", contests);

    AuditInfo subject =
      new AuditInfo("election type", Instant.now(), Instant.now(),
                    "12345678901234567890", new BigDecimal(0.05),
                    contestMap);

    assertEquals(subject.canonicalContests(), contestMap);
  }

  public RequestJSON contentsToUploadFile(final String json) {
    JsonParser parser = new JsonParser();
    JsonObject object = parser.parse(json).getAsJsonObject();
    RequestJSON f = gson.fromJson(object, RequestJSON.class);
    return f;
  }

  public String contestsFromJSON(final String json) {
    JsonParser parser;
    JsonObject object;

    try {
      parser = new JsonParser();
      object = parser.parse(json).getAsJsonObject();
      return object
        .getAsJsonArray("upload_file")
        .get(0)
        .getAsJsonObject()
        .get("contents")
        .getAsString();
    } catch (final Exception e) {
      System.out.println("JSON parse failure: " + e.getMessage());
      return "shark";
    }
  }

  @Test
  // @SuppressWarnings("PMD.DoNotUseThreads")
  // @SuppressFBWarnings(value = {"URF_UNREAD_FIELD"},
  //                   justification = "JSON blobs are big")
  public void testParsing() {
    final String json = "{\"election_date\":\"2018-07-31T06:00:00.000Z\",\"election_type\":\"coordinated\",\"public_meeting_date\":\"2018-08-07T06:00:00.000Z\",\"risk_limit\":0.05,\"upload_file\":[{\"preview\":\"blob:http://localhost:3000/54a4f865-9d2a-b442-881a-8630050977dc\",\"contents\":'\"CountyName\",\"ContestName\"\n\"Boulder\",\"Kombucha - DEM\"\n\"Boulder\",\"Kale - DEM\"\n\"Denver\",\"IPA - DEM\"\n\"Denver\",\"Porter - REP\"\n\"Chaffee\",\"Hooligan Race - DEM\"\n\"Chaffee\",\"Pole Pedal Paddle - DEM\"\n\"Chaffee\",\"Gunbarrel Challenge - REP\"\n'}]}";

    String contests = contestsFromJSON(json);

    assertEquals(contests,
                 "\"CountyName\",\"ContestName\"\n" +
                 "\"Boulder\",\"Kombucha - DEM\"\n" +
                 "\"Boulder\",\"Kale - DEM\"\n" +
                 "\"Denver\",\"IPA - DEM\"\n" +
                 "\"Denver\",\"Porter - REP\"\n" +
                 "\"Chaffee\",\"Hooligan Race - DEM\"\n" +
                 "\"Chaffee\",\"Pole Pedal Paddle - DEM\"\n" +
                 "\"Chaffee\",\"Gunbarrel Challenge - REP\"\n");

    try {
      ContestNameParser p = new ContestNameParser(contests);
      boolean successfulP = p.parse();
      assertEquals(p.contestCount(), OptionalInt.of(7));
    } catch (IOException ioe) { fail("Edge case", ioe); }
  }

  /**
   * A helper for writing nice JSON
   */
  public static class RequestJSON {
    private String contents = "";
    public String contents() { return contents; }
    public RequestJSON(String contents) { this.contents = contents; }
  }

  @Test
  public void testEmptyAuditInfo() {
    // Test an empty AuditInfo object
    AuditInfo ai = new AuditInfo();
    assertNull(ai.electionDate());
    assertNull(ai.electionType());
    assertNull(ai.seed());
    assertNull(ai.publicMeetingDate());
    assertNull(ai.riskLimit());

    Map<String, Set<String>> map = new TreeMap<>();
    assertEquals(ai.canonicalContests(), map);
    assertEquals(ai.getCanonicalChoices(), map);
    assertEquals(ai.hashCode(), nullableHashCode(null));

    assert(ai.equals(ai));
    assertFalse(ai.equals(""));

    assertEquals(ai.toString(),"AuditInfo[canonicalContests=" + map +
            ", electionType=null" +
            ", electionDate=null" +
            ", publicMeetingDate=null" +
            ", seed=null" +
            ", riskLimitnull");

  }
}
