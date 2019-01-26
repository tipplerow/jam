
package jam.pepmhc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.peptide.Peptide;

public final class AffinityCache {
    private final PepMHCPredictor predictor;
    private final Map<String, Map<Peptide, Double>> alleleMap;

    public AffinityCache(PepMHCPredictor predictor) {
        this.predictor = predictor;
        this.alleleMap = new HashMap<String, Map<Peptide, Double>>();
    }

    public Map<Peptide, Double> getAffinity(String allele, Collection<Peptide> peptides) {
        Map<Peptide, Double> innerMap    = getInnerMap(allele);
        Map<Peptide, Double> resultMap   = new HashMap<Peptide, Double>(peptides.size());
        Collection<Peptide>  computeList = new ArrayList<Peptide>();

        for (Peptide peptide : peptides) {
            Double affinity = innerMap.get(peptide);

            if (affinity != null)
                resultMap.put(peptide, affinity);
            else
                computeList.add(peptide);
        }

        if (!computeList.isEmpty()) {
            Map<Peptide, Double> computeMap = predictor.predictIC50(allele, computeList);

            innerMap.putAll(computeMap);
            resultMap.putAll(computeMap);
        }

        return resultMap;
    }

    private Map<Peptide, Double> getInnerMap(String allele) {
        Map<Peptide, Double> innerMap = alleleMap.get(allele);

        if (innerMap == null) {
            innerMap = new HashMap<Peptide, Double>();
            alleleMap.put(allele, innerMap);
        }

        return innerMap;
    }
}
