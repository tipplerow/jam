
package jam.miya;

import jam.lang.ObjectFactory;
import jam.mhc.GenotypeScanner;

public final class MiyaGenotypeScanner extends GenotypeScanner {
    private MiyaGenotypeScanner(String[] propertyFiles) {
        super(propertyFiles);
    }

    @Override protected ObjectFactory<MiyaMHC> getMHCFactory() {
        return MiyaMHC.GLOBAL_FACTORY;
    }

    public static void main(String[] args) {
        MiyaGenotypeScanner tuner = new MiyaGenotypeScanner(args);
        tuner.run();
    }
}
