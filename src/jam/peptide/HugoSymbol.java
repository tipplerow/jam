
package jam.peptide;

import jam.lang.KeyedObject;

/**
 * Represents HUGO Gene Nomenclature Committee (HGNC) identifiers.
 */
public final class HugoSymbol extends KeyedObject<String> {
    private HugoSymbol(String key) {
        super(key);
    }

    /**
     * Returns the HUGO symbol object for a given key string.
     *
     * @param key the HUGO symbol key.
     *
     * @return the HUGO symbol object for the given key string.
     */
    public static HugoSymbol instance(String key) {
        return new HugoSymbol(key);
    }
}
