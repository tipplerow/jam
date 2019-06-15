
package jam.rna;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.app.JamProperties;
import jam.hugo.HugoSymbol;
import jam.io.TableReader;
import jam.lang.JamException;
import jam.tcga.TumorBarcode;
import jam.util.MapUtil;

/**
 * Represents a gene expression profile in which a single profile
 * applies to an entire cohort. The aggregate profile is typically
 * the median expression in another proxy cohort.
 */
public final class AggregateProfile extends ExpressionProfile {
    private final Map<HugoSymbol, Expression> profile;

    private static AggregateProfile global = null;

    private AggregateProfile(Map<HugoSymbol, Expression> profile) {
        this.profile = Collections.unmodifiableMap(profile);
    }

    /**
     * Name of the system property that specifies the data file
     * containing the global aggregate expression profile.
     */
    public static final String PROFILE_FILE_NAME_PROPERTY =
        "jam.rna.aggregateExpressionProfile";

    /**
     * Returns the global expression profile defined by system
     * properties.
     *
     * @return the global expression profile defined by system
     * properties.
     */
    public static AggregateProfile global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static AggregateProfile createGlobal() {
        return load(resolveProfileFileName());
    }

    private static String resolveProfileFileName() {
        return JamProperties.getRequired(PROFILE_FILE_NAME_PROPERTY);
    }

    /**
     * Loads an aggregate expression profile from a data file.
     *
     * @param file the file to load.
     *
     * @return the aggregate expression profile contained in the
     * specified data file.
     *
     * @throws RuntimeException unless the specified file contains a
     * valid expression profile.
     */
    public static AggregateProfile load(File file) {
        Map<HugoSymbol, Expression> profile =
            new HashMap<HugoSymbol, Expression>();

        TableReader reader = TableReader.open(file);

        if (reader.columnKeys().size() != 2)
            throw JamException.runtime("Exactly two columns are required in aggregate profile [%s].", file);

        for (List<String> columns : reader) {
            HugoSymbol symbol = HugoSymbol.instance(columns.get(0));
            Expression level  = Expression.valueOf(columns.get(1));

            MapUtil.putUnique(profile, symbol, level);
        }

        return new AggregateProfile(profile);
    }

    /**
     * Loads an aggregate expression profile from a data file.
     *
     * @param fileName the name of the file to load.
     *
     * @return the aggregate expression profile contained in the
     * specified data file.
     *
     * @throws RuntimeException unless the specified file contains a
     * valid expression profile.
     */
    public static AggregateProfile load(String fileName) {
        return load(new File(fileName));
    }

    @Override public Expression lookup(TumorBarcode barcode, HugoSymbol symbol) {
        //
        // All tumors have the same expression for a given gene...
        //
        return profile.get(symbol);
    }
}
