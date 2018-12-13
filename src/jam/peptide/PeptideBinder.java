
package jam.peptide;

import jam.math.IntRange;

public interface PeptideBinder {
    
    public abstract AffinityModel getAffinityModel();

    public abstract Peptide getBinderPeptide();

    public abstract IntRange getBinderRegion();

    public abstract IntRange getTargetRegion();

    public default double computeAffinity(Peptide targetPeptide) {
        return getAffinityModel().computeAffinity(getBinderFragment(), getTargetFragment(targetPeptide));
    }

    public default Peptide getBinderFragment() {
        return getBinderPeptide().fragment(getBinderRegion());
    }        

    public default Peptide getTargetFragment(Peptide targetPeptide) {
        return targetPeptide.fragment(getTargetRegion());
    }        
}
