
package jam.peptide;

/**
 * Defines an affinity model with position-independent pairwise
 * interactions specified by the Miyazawa-Jernigan potential.
 */
public interface MJAffinityModel implements RIMAffinityModel {
    private MJAffinityModel() {}

    public static final MJAffinityModel INSTANCE = new MJAffinityModel();

    @Override public RIM getRIM() {
        return RIM.MiyazawaJernigan;
    }
}
