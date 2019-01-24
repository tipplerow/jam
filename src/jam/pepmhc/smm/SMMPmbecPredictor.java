
package jam.pepmhc.smm;

import jam.pepmhc.PredictionMethod;

public final class SMMPmbecPredictor extends MatrixPredictor {
    private SMMPmbecPredictor() {}

    /**
     * The single instance.
     */
    public static final SMMPmbecPredictor INSTANCE = new SMMPmbecPredictor();

    @Override public PredictionMethod getMethod() {
        return PredictionMethod.SMM_PMBEC;
    }
}
