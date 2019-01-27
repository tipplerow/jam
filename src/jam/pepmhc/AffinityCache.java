
package jam.pepmhc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.peptide.Peptide;

public final class AffinityCache {
    private final PepMHCPredictor predictor;
    private final Map<String, Map<Peptide, BindingRecord>> alleleMap;

    public AffinityCache(PepMHCPredictor predictor) {
        this.predictor = predictor;
        this.alleleMap = new HashMap<String, Map<Peptide, BindingRecord>>();
    }

    public Map<Peptide, BindingRecord> lookup(String allele, Collection<Peptide> peptides) {
        Collection<Peptide> computeList = new ArrayList<Peptide>();

        Map<Peptide, BindingRecord> innerMap  = getInnerMap(allele);
        Map<Peptide, BindingRecord> resultMap = new HashMap<Peptide, BindingRecord>(peptides.size());

        for (Peptide peptide : peptides) {
            BindingRecord record = innerMap.get(peptide);

            if (record != null)
                resultMap.put(peptide, record);
            else
                computeList.add(peptide);
        }

        if (!computeList.isEmpty()) {
            List<BindingRecord> newRecords = predictor.predict(allele, computeList);

            for (BindingRecord newRecord : newRecords) {
                innerMap.put(newRecord.getPeptide(), newRecord);
                resultMap.put(newRecord.getPeptide(), newRecord);
            }
        }

        return resultMap;
    }

    private Map<Peptide, BindingRecord> getInnerMap(String allele) {
        Map<Peptide, BindingRecord> innerMap = alleleMap.get(allele);

        if (innerMap == null) {
            innerMap = new HashMap<Peptide, BindingRecord>();
            alleleMap.put(allele, innerMap);
        }

        return innerMap;
    }
}
