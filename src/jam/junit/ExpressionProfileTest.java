
package jam.junit;

import java.io.File;

import jam.hugo.HugoSymbol;
import jam.rna.Expression;
import jam.rna.ExpressionProfile;

import org.junit.*;
import static org.junit.Assert.*;

public class ExpressionProfileTest {
    private static final HugoSymbol A1BG  = HugoSymbol.instance("A1BG");
    private static final HugoSymbol A2BP1 = HugoSymbol.instance("A2BP1");
    private static final HugoSymbol A2LD1 = HugoSymbol.instance("A2LD1");
    private static final HugoSymbol XYZ   = HugoSymbol.instance("XYZ");

    private static final String FILE_NAME = "data/test/expression_profile.csv";

    @Test public void testGet() {
        ExpressionProfile profile = ExpressionProfile.load(FILE_NAME);
        assertProfile(profile);
    }

    private void assertProfile(ExpressionProfile profile) {
        assertEquals(Expression.valueOf(10373.7), profile.get(A1BG));
        assertEquals(Expression.valueOf(17.2911), profile.get(A2BP1));
        assertEquals(Expression.valueOf(182.392), profile.get(A2LD1));
        assertEquals(Expression.valueOf(0.0), profile.get(XYZ));
    }

    @Test public void testLoadStore() {
        File tmpFile = new File("data/test/_tmp_expression_profile.csv.gz");
        tmpFile.deleteOnExit();

        ExpressionProfile profile1 = ExpressionProfile.load(FILE_NAME);
        profile1.store(tmpFile);

        ExpressionProfile profile2 = ExpressionProfile.load(tmpFile);
        assertProfile(profile2);

    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ExpressionProfileTest");
    }
}
