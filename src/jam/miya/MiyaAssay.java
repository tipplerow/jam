
package jam.miya;

import jam.lang.ObjectFactory;
import jam.neo.NeoAntigenAssay;
import jam.peptide.Peptide;
import jam.tcell.TCR;
import jam.tcell.TCellProperties;

public final class MiyaAssay extends NeoAntigenAssay {
    private MiyaAssay(String[] propertyFiles) {
        super(propertyFiles);
    }

    @Override protected ObjectFactory<? extends Peptide> getPeptideFactory() {
        return Peptide.nativeFactory(TCellProperties.getReceptorLength());
    }

    @Override protected ObjectFactory<? extends TCR> getTCRFactory() {
        return MiyaTCR.GLOBAL_FACTORY;
    }

    public static void main(String[] args) {
        MiyaAssay tuner = new MiyaAssay(args);
        tuner.run();
    }
}
