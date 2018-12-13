
package jam.tcell;

import jam.math.IntRange;
import jam.peptide.AffinityModel;
import jam.peptide.Peptide;

public abstract class AbstractTCR implements TCR {
    protected final Peptide binder;

    protected AbstractTCR(Peptide binder) {
        this.binder = binder;
    }

    public abstract AffinityModel getAffinityModel();

    @Override public double computeAffinity(Peptide target) {
        return getAffinityModel().computeAffinity(binder, target.fragment(TCellProperties.getTargetRegion()));
    }

    @Override public double getPositiveThreshold() {
        return TCellProperties.getPositiveThreshold();
    }

    @Override public double getNegativeThreshold() {
        return TCellProperties.getNegativeThreshold();
    }
}
