
package jam.junit;

import java.util.Arrays;
import java.util.List;

import jam.agpro.AntigenProcessor;
import jam.chop.NetChop;
import jam.peptide.Peptide;
import jam.tap.TAP;

import org.junit.*;
import static org.junit.Assert.*;

public class AntigenProcessorTest extends NumericTestBase {
    private static final Peptide A3GALT2 =
        Peptide.parse("MALKEGLRAWKRIFWRQILLTLGLLGLFLYGLPKFRHLEALIPMGVCPSATMSQLRDNFT" +
                      "GALRPWARPEVLTCTPWGAPIIWDGSFDPDVAKQEARQQNLTIGLTIFAVGRYLEKYLER" +
                      "FLETAEQHFMAGQSVMYYVFTELPGAVPRVALGPGRRLPVERVARERRWQDVSMARMRTL" +
                      "HAALGGLPGREAHFMFCMDVDQHFSGTFGPEALAESVAQLHSWHYHWPSWLLPFERDAHS" +
                      "AAAMAWGQGDFYNHAAVFGGSVAALRGLTAHCAGGLDWDRARGLEARWHDESHLNKFFWL" +
                      "HKPAKVLSPEFCWSPDIGPRAEIRRPRLLWAPKGYRLLRN");

    private static final Peptide BEX4 =
        Peptide.parse("MESKEELAANNLNGENAQQENEGGEQAPTQNEEESRHLGGGEGQKPGGNIRRGRVRRLVP" +
                      "NFRWAIPNRHIEHNEARDDVERFVGQMMEIKRKTREQQMRHYMRFQTPEPDNHYDFCLIP");

    @Test public void testNetChopTAPLenient() {
        AntigenProcessor processor =
            AntigenProcessor.resolve("data/test/NetChop_TAP_Lenient.prop");

        NetChop netChop = processor.getNetChop();

        assertTrue(Arrays.equals(new int[] { 9, 10 }, netChop.getLengths()));
        assertDouble(0.5, netChop.getThreshold());

        TAP tap = processor.getTAP();

        assertDouble(0.2, tap.getAlpha());
        assertDouble(1.0, tap.getThreshold());

        if (!NetChop.isInstalled())
            return;

        List<Peptide> fragments = processor.process(BEX4);
        System.out.println(fragments.size());
        System.out.println(fragments);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.AntigenProcessorTest");
    }
}
