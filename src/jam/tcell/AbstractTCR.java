
package jam.tcell;

import jam.peptide.Peptide;

public abstract class AbstractTCR implements TCR {
    protected final Peptide binder;

    protected AbstractTCR(Peptide binder) {
        this.binder = binder;
    }

    @Override public double getActivationEnergy() {
        return TCellProperties.getActivationEnergy();
    }

    @Override public double getPositiveThreshold() {
        return TCellProperties.getPositiveThreshold();
    }

    @Override public double getNegativeThreshold() {
        return TCellProperties.getNegativeThreshold();
    }
}
