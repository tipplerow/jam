
package jam.bio;

/**
 * Represents peptide binders with <em>position-independent</em> local
 * pairwise interactions defined by the Miyazawa-Jernigan potential.
 */
public interface MJBinder extends RIMBinder {
    /**
     * Returns the residue-interaction matrix that defines the
     * pairwise potential.
     *
     * @return the pairwise free energy.
     */
    @Override public RIM getRIM() {
        return RIM.MiyazawaJernigan;
    }
}
