
package jam.rna;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.io.TableReader;
import jam.lang.JamException;
import jam.peptide.HugoSymbol;
import jam.tcga.TumorBarcode;
import jam.util.MapUtil;

final class AggregateProfile extends ExpressionProfile {
    private final Map<HugoSymbol, Expression> profile;

    private AggregateProfile(Map<HugoSymbol, Expression> profile) {
        this.profile = Collections.unmodifiableMap(profile);
    }

    static AggregateProfile load(String fileName) {
        Map<HugoSymbol, Expression> profile =
            new HashMap<HugoSymbol, Expression>();

        TableReader reader = TableReader.open(fileName);

        if (reader.columnKeys().size() != 2)
            throw JamException.runtime("Exactly two columns are required in aggregate profile [%s].", fileName);

        for (List<String> columns : reader) {
            HugoSymbol symbol = HugoSymbol.instance(columns.get(0));
            Expression level  = Expression.valueOf(columns.get(1));

            MapUtil.putUnique(profile, symbol, level);
        }

        return new AggregateProfile(profile);
    }

    @Override public Expression lookup(TumorBarcode barcode, HugoSymbol symbol) {
        //
        // All tumors have the same expression for a given gene...
        //
        return profile.get(symbol);
    }
}
