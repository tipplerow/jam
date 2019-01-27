
package jam.pepmhc.smm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.pepmhc.BindingRecord;
import jam.pepmhc.PepMHCPredictor;
import jam.peptide.Peptide;

public abstract class MatrixPredictor implements PepMHCPredictor {
    private final Map<String, Map<Integer, StabilizedMatrix>> outerMap;

    protected MatrixPredictor() {
        this.outerMap = new HashMap<String, Map<Integer, StabilizedMatrix>>();
    }

    @Override public BindingRecord predict(String allele, Peptide peptide) {
        return new BindingRecord(peptide, getMatrix(allele, peptide.length()).computeIC50(peptide));
    }

    @Override public List<BindingRecord> predict(String allele, Collection<Peptide> peptides) {
        List<BindingRecord> records = new ArrayList<BindingRecord>(peptides.size());

        for (Peptide peptide : peptides)
            records.add(predict(allele, peptide));

        return records;
    }

    private StabilizedMatrix getMatrix(String allele, int length) {
        StabilizedMatrix matrix = getInnerMap(allele).get(length);

        if (matrix == null) {
            matrix = StabilizedMatrix.instance(getMethod(), allele, length);
            getInnerMap(allele).put(length, matrix);
        }

        return matrix;
    }

    private Map<Integer, StabilizedMatrix> getInnerMap(String allele) {
        Map<Integer, StabilizedMatrix> innerMap = outerMap.get(allele);

        if (innerMap == null) {
            innerMap = new HashMap<Integer, StabilizedMatrix>();
            outerMap.put(allele, innerMap);
        }

        return innerMap;
    }
}
