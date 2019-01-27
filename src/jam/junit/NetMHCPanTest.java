
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
    static {
        System.setProperty(NetMHCPan.EXECUTABLE_PATH_PROPERTY, "/Users/scott/local/netMHCpan-4.0/netMHCpan");
    }

    @Test public void testExecutable() {
        System.out.print("NetMHCPan ");
        System.out.print(NetMHCPan.isInstalled() ? "IS" : "is NOT");
        System.out.print(" installed.");
        System.out.println();
    }

    @Test public void testPredict1() {
        if (!NetMHCPan.isInstalled())
            return;

        assertEquals( 8706.7, NetMHCPan.INSTANCE.predict("HLA-A*02:01", Peptide.parse("AEFGPWQTV")).getAffinity(), 0.1);
        assertEquals(33803.5, NetMHCPan.INSTANCE.predict("HLA-A*01:01", Peptide.parse("AEFGPWQTV")).getAffinity(), 0.1);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.NetMHCPanTest");
    }
}
