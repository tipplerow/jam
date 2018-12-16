
package jam.peptide;

import jam.math.IntRange;
import jam.mhc.MHCProperties;
import jam.tcell.TCellProperties;

/**
 * Manages global system properties related to peptides that interact
 * with MHC molecules and T cell receptors.
 */
public final class TargetProperties {
    private static Integer targetLength = null;

    /**
     * Returns the length of all target peptides implied by the target
     * binding regions for the MHC molecules and T cell receptors.
     *
     * @return the length of all target peptides.
     */
    public static int getTargetLength() {
        if (targetLength == null)
            resolveTargetLength();

        return targetLength;
    }

    private static void resolveTargetLength() {
        IntRange mhcRegion = MHCProperties.getTargetRegion();
        IntRange tcrRegion = TCellProperties.getTargetRegion();

        targetLength = Math.max(mhcRegion.upper(), tcrRegion.upper()) + 1;
    }
}
