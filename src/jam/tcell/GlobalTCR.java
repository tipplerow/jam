
package jam.tcell;

/**
 * Represents a T cell receptor with binding parameters defined by
 * global system properties.
 */
public abstract class GlobalTCR implements TCR {
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
