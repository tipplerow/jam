
package jam.junit;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

import jam.io.IOUtil;
import jam.pepmhc.net.NetMHCPan;
import jam.peptide.Peptide;
import jam.peptide.Residue;

import org.junit.*;
import static org.junit.Assert.*;

public class NetMHCPanTest extends NumericTestBase {
    private static final String TEST_OUTPUT_FILE = "data/test/test.pep.out";

    static {
        System.setProperty(NetMHCPan.EXECUTABLE_PATH_PROPERTY, "/Users/scott/local/netMHCpan-4.0/netMHCpan");
    }

    @Test public void testExecutable() {
        System.out.print("NetMHCPan ");
        System.out.print(NetMHCPan.isInstalled() ? "IS" : "is NOT");
        System.out.print(" installed.");
        System.out.println();
    }

    @Test public void testParseOutput() {
        BufferedReader reader = IOUtil.openReader(TEST_OUTPUT_FILE);
        Map<Peptide, Double> affinities = NetMHCPan.parseOutput(reader);

        assertEquals(10, affinities.size());

        assertEquals(    7.1, affinities.get(Peptide.parse("AAAWYLWEV")), 0.1);
        assertEquals( 8706.7, affinities.get(Peptide.parse("AEFGPWQTV")), 0.1);
        assertEquals(25420.6, affinities.get(Peptide.parse("AASSTHRKV")), 0.1);
    }

    @Test public void testPredict1() {
        if (!NetMHCPan.isInstalled())
            return;

        assertEquals( 8706.7, NetMHCPan.INSTANCE.predictIC50("HLA-A*02:01", Peptide.parse("AEFGPWQTV")), 0.1);
        assertEquals(33803.5, NetMHCPan.INSTANCE.predictIC50("HLA-A*01:01", Peptide.parse("AEFGPWQTV")), 0.1);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.NetMHCPanTest");
    }
}
