
package jam.util;

import java.util.Map;
import java.util.TreeMap;

import jam.lang.ObjectFactory;

/**
 * Implements an indexed table of values with two keys using tree maps
 * for the underlying storage.
 */
public class PairKeyTreeTable<K1, K2, V> extends PairKeyTable<K1, K2, V> {
    /**
     * Creates a new empty table using {@code TreeMap}s for the
     * underlying storage.
     */
    protected PairKeyTreeTable() {
        super(createOuterMap(), createInnerMapFactory());
    }

    private static <K1, K2, V> Map<K1, Map<K2, V>> createOuterMap() {
        return new TreeMap<K1, Map<K2, V>>();
    }

    private static <K2, V> ObjectFactory<Map<K2, V>> createInnerMapFactory() {
        return new ObjectFactory<Map<K2, V>>() {
            @Override public Map<K2, V> newInstance() {
                return new TreeMap<K2, V>();
            };
        };
    }

    /**
     * Creates a new empty table using {@code TreeMap}s for the
     * underlying storage.
     *
     * @param <K1> runtime type for the outer keys.
     *
     * @param <K2> runtime type for the inner keys.
     *
     * @param <V> runtime type for the values.
     *
     * @return a new empty table using {@code TreeMap}s for the
     * underlying storage.
     */
    public static <K1, K2, V> PairKeyTreeTable<K1, K2, V> create() {
        return new PairKeyTreeTable<K1, K2, V>();
    }
}
