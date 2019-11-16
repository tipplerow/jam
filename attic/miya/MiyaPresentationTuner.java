
package jam.miya;

import java.util.ArrayList;
import java.util.List;

import jam.mhc.MHCProperties;
import jam.mhc.PresentationTuner;
import jam.peptide.Peptide;

public final class MiyaPresentationTuner extends PresentationTuner {
    private MiyaPresentationTuner(String[] propertyFiles) {
        super(propertyFiles);
    }

    @Override protected List<MiyaMHC> enumerateAlleles() {
        List<Peptide> anchors = MHCProperties.enumerateAnchorPeptides();
        List<MiyaMHC> alleles = new ArrayList<MiyaMHC>(anchors.size());

        for (Peptide anchor : anchors)
            alleles.add(new MiyaMHC(anchor));

        return alleles;
    }

    public static void main(String[] args) {
        MiyaPresentationTuner tuner = new MiyaPresentationTuner(args);
        tuner.run();
    }
}
