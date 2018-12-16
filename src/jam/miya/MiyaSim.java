
package jam.miya;

import jam.lang.ObjectFactory;
import jam.mhc.MHC;
import jam.neo.NeoAntigenSim;
import jam.peptide.Peptide;
import jam.peptide.TargetProperties;
import jam.tcell.TCR;

public final class MiyaSim extends NeoAntigenSim {
    private MiyaSim(String[] propertyFiles) {
        super(propertyFiles);
    }

    @Override protected ObjectFactory<MiyaMHC> getMHCFactory() {
        return MiyaMHC.GLOBAL_FACTORY;
    }

    @Override protected ObjectFactory<? extends Peptide> getPeptideFactory() {
        return Peptide.nativeFactory(TargetProperties.getTargetLength());
    }

    @Override protected ObjectFactory<? extends TCR> getTCRFactory() {
        return MiyaTCR.GLOBAL_FACTORY;
    }

    public static void main(String[] args) {
        MiyaSim simulator = new MiyaSim(args);
        simulator.run();
    }
}
