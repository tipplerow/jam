
package jam.miya;

import jam.lang.ObjectFactory;
import jam.peptide.Peptide;
import jam.peptide.RIM;
import jam.tcell.PairwiseTCR;
import jam.tcell.TCellProperties;

/**
 * Represents a T cell receptor that binds peptides with a free energy
 * defined by the Miyazawa-Jernigan pairwise potential.
 */
public final class MiyaTCR extends PairwiseTCR {
    /**
     * Creates a new Miyazawa-Jernigan T cell receptor with a fixed
     * binding structure.
     *
     * @param binder the peptide binding structure.
     */
    public MiyaTCR(Peptide binder) {
        super(binder);
    }

    /**
     * A factory that creates Miyazawa-Jernigan T cell receptors with
     * randomly generated binding structures having the common global
     * length.
     */
    public static ObjectFactory<MiyaTCR> GLOBAL_FACTORY = new GlobalFactory();

    private static final class GlobalFactory implements ObjectFactory<MiyaTCR> {
        @Override public MiyaTCR newInstance() {
            return MiyaTCR.newInstance();
        }
    }

    /**
     * Creates a new Miyazawa-Jernigan T cell receptor with a randomly
     * generated binding structure with the common global length.
     *
     * @return a new Miyazawa-Jernigan T cell receptor with a randomly
     * generated binding structure with the common global length.
     */
    public static MiyaTCR newInstance() {
        return newInstance(TCellProperties.getReceptorLength());
    }

    /**
     * Creates a new Miyazawa-Jernigan T cell receptor with a randomly
     * generated binding structure of a given length.
     *
     * @param length the length of the binding structure.
     *
     * @return a new Miyazawa-Jernigan T cell receptor with a randomly
     * generated binding structure having the specified length.
     */
    public static MiyaTCR newInstance(int length) {
        return new MiyaTCR(Peptide.newNative(length));
    }

    @Override public RIM getRIM() {
        return RIM.MiyazawaJernigan;
    }
}
