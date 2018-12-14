
package jam.miya;

import jam.lang.ObjectFactory;
import jam.mhc.MHCProperties;
import jam.mhc.PairwiseMHC;
import jam.peptide.Peptide;
import jam.peptide.RIM;
import jam.peptide.Residue;

/**
 * Represents an MHC molecule that binds peptides with a free energy
 * defined by the Miyazawa-Jernigan pairwise potential.
 */
public final class MiyaMHC extends PairwiseMHC {
    /**
     * Creates a new Miyazawa-Jernigan MHC molecule with a fixed
     * anchor structure.
     *
     * @param anchor the peptide anchor structure.
     */
    public MiyaMHC(Peptide anchor) {
        super(anchor);
    }

    /**
     * A factory that creates Miyazawa-Jernigan MHC molecules with
     * randomly generated anchors having the common global length.
     */
    public static ObjectFactory<MiyaMHC> GLOBAL_FACTORY = new GlobalFactory();

    private static final class GlobalFactory implements ObjectFactory<MiyaMHC> {
        @Override public MiyaMHC newInstance() {
            return MiyaMHC.newInstance();
        }
    }

    /**
     * Creates a new Miyazawa-Jernigan MHC molecule with a randomly
     * generated anchor structure with the common global length.
     *
     * @return a new Miyazawa-Jernigan MHC molecule with a randomly
     * generated anchor structure with the common global length.
     */
    public static MiyaMHC newInstance() {
        return newInstance(MHCProperties.getAnchorLength());
    }

    /**
     * Creates a new Miyazawa-Jernigan MHC molecule with a randomly
     * generated anchor structure of a given length.
     *
     * @param length the length of the anchor structure.
     *
     * @return a new Miyazawa-Jernigan MHC molecule with a randomly
     * generated anchor structure having the specified length.
     */
    public static MiyaMHC newInstance(int length) {
        return new MiyaMHC(Peptide.newNative(length));
    }

    /**
     * Returns a Miyazawa-Jernigan MHC molecule with a fixed anchor
     * structure.
     *
     * @param residues the residues in the anchor structure.
     *
     * @return a Miyazawa-Jernigan MHC molecule with the specified
     * anchor structure.
     */
    public static MiyaMHC of(Residue... residues) {
        return new MiyaMHC(Peptide.of(residues));
    }

    @Override public RIM getRIM() {
        return RIM.MiyazawaJernigan;
    }
}
