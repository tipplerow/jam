
package jam.tcell.miya;

import jam.bio.MJBinder;
import jam.bio.Peptide;
import jam.bio.RIM;
import jam.bio.Residue;
import jam.lang.ObjectFactory;
import jam.math.IntRange;
import jam.tcell.GlobalTCR;
import jam.tcell.TCellProperties;

/**
 * Represents a T cell receptor that binds peptides with a free energy
 * defined by the Miyazawa-Jernigan pairwise potential.
 */
public final class MiyaTCR extends GlobalTCR implements MJBinder {
    private final Peptide binder;

    /**
     * Creates a new Miyazawa-Jernigan T cell receptor with a fixed
     * binding structure.
     *
     * @param binder the peptide binding structure.
     */
    public MiyaTCR(Peptide binder) {
        this.binder = binder;
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

    @Override public Peptide getBinderPeptide() {
        return binder;
    }

    @Override public IntRange getTargetRegion() {
        return TCellProperties.getTargetRegion();
    }

    @Override public double meanFreeEnergy() {
        double mean = 0.0;

        for (Residue residue : binder)
            mean += RIM.MiyazawaJernigan.mean(residue);

        return mean;
    }
}
