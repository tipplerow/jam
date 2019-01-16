
package jam.pepmhc;

import java.util.HashMap;
import java.util.Map;

import jam.lang.JamException;
import jam.pepmhc.smm.SMMPredictor;

final class PredictorCache {
    private final Map<PredictorKey, PepMHCPredictor> predictors;

    private PredictorCache() {
        this.predictors = new HashMap<PredictorKey, PepMHCPredictor>();
    }

    static PredictorCache INSTANCE = new PredictorCache();

    PepMHCPredictor get(PredictorKey key) {
        PepMHCPredictor predictor = predictors.get(key);

        if (predictor == null) {
            predictor = newPredictor(key);
            predictors.put(key, predictor);
        }

        return predictor;
    }

    private static PepMHCPredictor newPredictor(PredictorKey key) {
        PredictionMethod method = key.getPredictionMethod();

        switch (key.getPredictionMethod()) {
        case SMM:
            // Fall through
        case SMM_PMBEC:
            return SMMPredictor.instance(key);

        default:
            throw JamException.runtime("Unknown prediction method: [%s].", method);
        }
    }
}

