
package jam.junit;

import jam.bio.Residue;
import jam.bio.RIM;

import org.junit.*;
import static org.junit.Assert.*;

public class RIMTest extends NumericTestBase {
    @Test public void testMJ() {
        RIM MJ = RIM.MiyazawaJernigan;

        assertDouble(-5.44, MJ.get(Residue.Cys, Residue.Cys));

        assertDouble(-6.29, MJ.get(Residue.Val, Residue.Phe));
        assertDouble(-6.29, MJ.get(Residue.Phe, Residue.Val));

        assertDouble(-1.53, MJ.get(Residue.Pro, Residue.Asn));
        assertDouble(-1.53, MJ.get(Residue.Asn, Residue.Pro));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.RIMTest");
    }
}
