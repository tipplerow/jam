
package jam.miya;

import jam.lang.ObjectFactory;
import jam.peptide.Peptide;
import jam.tcell.TCR;
import jam.tcell.TCellProperties;
import jam.tcell.SelectionTuner;

public final class MiyaSelectionTuner extends SelectionTuner {
    private MiyaSelectionTuner(String[] propertyFiles) {
        super(propertyFiles);
    }

    @Override protected ObjectFactory<? extends Peptide> getPeptideFactory() {
        return Peptide.nativeFactory(TCellProperties.getReceptorLength());
    }

    @Override protected ObjectFactory<? extends TCR> getTCRFactory() {
        return MiyaTCR.GLOBAL_FACTORY;
    }

    public static void main(String[] args) {
        MiyaSelectionTuner tuner = new MiyaSelectionTuner(args);
        tuner.run();
    }
}
