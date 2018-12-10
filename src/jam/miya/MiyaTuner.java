
package jam.miya;

import jam.bio.Peptide;
import jam.lang.ObjectFactory;
import jam.tcell.TCR;
import jam.tcell.TCellProperties;
import jam.tcell.ThresholdTuner;

public final class MiyaTuner extends ThresholdTuner {
    private MiyaTuner(String[] propertyFiles) {
        super(propertyFiles);
    }

    @Override protected ObjectFactory<? extends Peptide> getPeptideFactory() {
        return Peptide.nativeFactory(TCellProperties.getReceptorLength());
    }

    @Override protected ObjectFactory<? extends TCR> getTCRFactory() {
        return MiyaTCR.GLOBAL_FACTORY;
    }

    public static void main(String[] args) {
        MiyaTuner tuner = new MiyaTuner(args);
        tuner.run();
    }
}
