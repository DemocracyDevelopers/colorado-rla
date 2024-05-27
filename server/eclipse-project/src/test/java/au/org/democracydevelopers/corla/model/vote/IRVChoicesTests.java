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

package au.org.democracydevelopers.corla.model.vote;

// import au.org.democracydevelopers.model.b;
import au.org.democracydevelopers.corla.model.vote.IRVChoices;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.junit.Assert.*;

public class IRVChoicesTests {

    /*
     * Using the DuplicatesBeforeOvervotes function and testing against the
     * specific examples in the Guide.
     */
    @Test
    void Example1OvervotesNoValidRankings() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Candidate A(1),Candidate B(1),Candidate C(1),Candidate C(2),Candidate B(3)");
      String expectedInterpretation = "";

      IRVChoices interpretation = b.InterpretValidIntent();

      assertEquals(interpretation.toString(), expectedInterpretation);

    }

    @Test
    void Example2OvervoteWithValidRankings() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Candidate B(1),Candidate A(2),Candidate C(2),Candidate C(3)");
      String expectedInterpretation = "Candidate B(1)";

      IRVChoices interpretation = b.InterpretValidIntent();

      assertEquals(interpretation.toString(), expectedInterpretation);

    }

    @Test
    void Example1SkippedRankings() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Candidate A(1),Candidate B(3)");
      String expectedInterpretation = "Candidate A(1)";

      IRVChoices interpretation = b.InterpretValidIntent();

      assertEquals(interpretation.toString(), expectedInterpretation);

    }

    @Test
    void Example1DuplicateRankings() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Candidate A(1),Candidate A(2),Candidate B(3)");
      String expectedInterpretation = "Candidate A(1)";

      IRVChoices interpretation = b.InterpretValidIntent();

      assertEquals(interpretation.toString(), expectedInterpretation);

    }

    @Test
    void Example1DuplicatesAndOvervotes() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Candidate B(1),Candidate A(2),Candidate C(2),Candidate C(3)");
      String expectedInterpretation = "Candidate B(1)";

      IRVChoices interpretation = b.InterpretValidIntent();

      assertEquals(interpretation.toString(), expectedInterpretation);

    }

    @Test
    void Example2DuplicatesAndOvervotes() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Candidate B(1),Candidate A(2),Candidate B(2),Candidate C(3)");
      String expectedInterpretation ="Candidate B(1),Candidate A(2),Candidate C(3)";

      IRVChoices interpretation = b.InterpretValidIntent();

      assertEquals(interpretation.toString(), expectedInterpretation);

    }

    /*
     * Other general validity tests for ballot interpretation,
     * including applying single rules and checking the validity
     * of the output.
     */
    @Test
    void testValidBallots() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1)");
      assertTrue(b.IsValid());
      assert b.IsValid();

      b = new IRVChoices("Alice(1),Bob(2)");
      assert b.IsValid();

      b = new IRVChoices("Alice(1),Chuan(3),Bob(2)");
      assert b.IsValid();

      b = new IRVChoices("Diego(4),Alice(1),Chuan(3),Bob(2)");
      assert b.IsValid();
    }

    @Test
    void testInvalidBallot() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Alice(1)");
      assert !b.IsValid();

      b = new IRVChoices("Alice(1),Alice(2)");
      assert !b.IsValid();

      b = new IRVChoices("Alice(1),Bob(1)");
      assert !b.IsValid();

      b = new IRVChoices("Alice(1),Bob(3)");
      assert !b.IsValid();

      b = new IRVChoices("Alice(2),Bob(2)");
      assert !b.IsValid();

      b = new IRVChoices("Alice(2),Bob(3)");
      assert !b.IsValid();
    }

    @Test
    void applyRule1Test1() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Bob(2)");
      IRVChoices i = b.ApplyRule1();
      assert i.getLength() == 2;
    }

    @Test
    void applyRule1Test2() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Alice(1)");
      IRVChoices i = b.ApplyRule1();
      assert i.getLength() == 0;
    }

    @Test
    void applyRule1Test3() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(2),Alice(1)");
      IRVChoices i = b.ApplyRule1();
      assert i.getLength() == 2;
    }

    @Test
    void applyRule1Test4() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(2)");
      IRVChoices i = b.ApplyRule1();
      assert i.getLength() == 1;
    }

    @Test
    void applyRule1Test5() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(2),Diego(3)");
      IRVChoices i = b.ApplyRule1();
      assert i.getLength() == 1;
    }

    @Test
    void applyRule1Test6() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(3),Diego(3)");
      IRVChoices i = b.ApplyRule1();
      assert i.getLength() == 2;
    }

    @Test
    void applyRule2Test1() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Bob(2)");
      IRVChoices i2 = b.ApplyRule2();
      assert i2.getLength() == 2;
    }

    @Test
    void applyRule2Test2() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Alice(1)");
      IRVChoices i2 = b.ApplyRule2();
      assert i2.getLength() == 2;
    }

    @Test
    void applyRule2Test3() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(2),Alice(1)");
      IRVChoices i2 = b.ApplyRule2();
      assert i2.getLength() == 2;
    }

    @Test
    void applyRule2Test4() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(3)");
      IRVChoices i2 = b.ApplyRule2();
      assert i2.getLength() == 3;
    }

    @Test
    void applyRule2Test5() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Bob(3)");
      IRVChoices i2 = b.ApplyRule2();
      assert i2.getLength() == 1;
    }

    @Test
    void applyRule2Test6() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Bob(2),Chuan(3)");
      IRVChoices i2 = b.ApplyRule2();
      assert i2.getLength() == 0;
    }

    @Test
    void applyRule2Test7() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(4)");
      IRVChoices i2 = b.ApplyRule2();
      assert i2.getLength() == 2;
    }

    @Test
    void applyRule2Test8() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(3),Bob(2),Chuan(4)");
      IRVChoices i2 = b.ApplyRule2();
      assert i2.getLength() == 0;
    }

    @Test
    void applyRule1AndRule2Test1() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(2),Diego(4)");
      IRVChoices i = b.ApplyRule1();
      IRVChoices i2 = i.ApplyRule2();
      assert i2.getLength() == 1;
    }

    @Test
    void applyRule1AndRule2Test2() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(2),Diego(4)");
      IRVChoices i2 = b.ApplyRule2();
      IRVChoices i1 = i2.ApplyRule1();
      assert i1.getLength() == 1;
    }

    @Test
    void applyRule3Test1() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Bob(2)");
      IRVChoices i3 = b.ApplyRule3();
      assert i3.getLength() == 2;
    }

    @Test
    void applyRule3Test2() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Alice(1)");
      IRVChoices i3 = b.ApplyRule3();
      assert i3.getLength() == 1;
    }

    @Test
    void applyRule3Test3() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(2),Alice(1)");
      IRVChoices i3 = b.ApplyRule3();
      assert i3.getLength() == 1;
    }

    @Test
    void applyRule3Test4() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(3)");
      IRVChoices i3 = b.ApplyRule3();
      assert i3.getLength() == 3;
    }

    @Test
    void applyRule3Test5() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Bob(3)");
      IRVChoices i3 = b.ApplyRule3();
      assert i3.getLength() == 2;
    }

    @Test
    void applyRule3Test6() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(2)");
      IRVChoices i3 = b.ApplyRule3();
      assert i3.getLength() == 2;
    }

    @Test
    void applyRule3Test7() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(2),Chuan(4),Bob(3)");
      IRVChoices i3 = b.ApplyRule3();
      assert i3.getLength() == 3;
    }

    @Test
    void applyRule3Test8() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Alice(2),Alice(4)");
      IRVChoices i3 = b.ApplyRule3();
      assert i3.getLength() == 1;
    }
    @Test
    void applyRule3AndRule2Test1() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(2)");
      IRVChoices i3 = b.ApplyRule3();
      IRVChoices i2 = i3.ApplyRule2();
      assert i2.getLength() == 2;
      assert i2.IsValid();
    }

    @Test
    void applyRule3AndRule2Test3() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(3)");
      IRVChoices i3 = b.ApplyRule3();
      IRVChoices i2 = i3.ApplyRule2();
      assert i2.getLength() == 1;
      assert i2.IsValid();
    }

    @Test
    void applyRule2AndRule3Test1() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(2)");
      IRVChoices i2 = b.ApplyRule2();
      IRVChoices i3 = i2.ApplyRule3();
      assert i3.getLength() == 2;
      assert i3.IsValid();
    }

    @Test
    void applyRule2AndRule3Test3() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(3)");
      IRVChoices i2 = b.ApplyRule2();
      IRVChoices i3 = i2.ApplyRule3();
      assert i3.getLength() == 2;
      assert !i3.IsValid();
    }
    @Test
    void applyRule1AndRule3Test1() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(2)");
      IRVChoices i1 = b.ApplyRule1();
      IRVChoices i3 = i1.ApplyRule3();
      assert i3.getLength() == 1;
      assert i3.IsValid();
    }

    @Test
    void applyRule3AndRule1Test1() throws IRVParsingException {
      IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(2)");
      IRVChoices i3 = b.ApplyRule3();
      IRVChoices i1 = i3.ApplyRule1();
      assert i1.getLength() == 2;
      assert i1.IsValid();
    }
  }
