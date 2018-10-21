
package jam.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

import jam.lang.ObjectFactory;

/**
 * Provides utility operations on multisets.
 */
public final class MultisetUtil {
    private MultisetUtil() {}

    /**
     * Counts the number of unique items in a multiset.
     *
     * @param set the set to examine.
     *
     * @return the number of unique items in the given multiset.
     */
    public static int countUnique(Multiset set) {
        return set.elementSet().size();
    }

    /**
     * Computes the frequency at which an items appears in a multiset.
     *
     * @param set the multiset to examine.
     *
     * @param key the key to search for.
     *
     * @return the frequency at which the specified item appears in
     * the multiset: {@code set.count(key) / ((double) set.size())}.
     */
    public static double frequency(Multiset set, Object key) {
        return set.count(key) / ((double) set.size());
    }

    /**
     * Computes the frequency of occurrence for each item in a
     * multiset.
     *
     * @param <E> the type of element stored in the multiset.
     *
     * @param set the multiset to examine.
     *
     * @return a map containing the frequency of occurrence for each
     * item in the multiset.
     */
    public static <E> Map<E, Double> frequencyMap(Multiset<E> set) {
        Set<E> keys = set.elementSet();
        Map<E, Double> map = new HashMap<E, Double>(keys.size());

        for (E key : keys)
            map.put(key, frequency(set, key));

        return map;
    }

    /**
     * Creates a new {@code HashMultiset} populated by a sequence of
     * integers.
     *
     * @param values the values to populate the new multiset.
     *
     * @return a new {@code HashMultiset} counting the given integers.
     */
    public static HashMultiset<Integer> hash(int... values) {
        HashMultiset<Integer> set = HashMultiset.create();

        for (int value : values)
            set.add(value);

        return set;
    }

    /**
     * Creates a new {@code HashMultiset} with contents taken from a
     * series of collections.
     *
     * @param <E> the type of element to be stored in the multiset.
     *
     * @param collections the collections to populate the new multiset.
     *
     * @return a new {@code HashMultiset} containing all items in the
     * given collections.
     */
    @SuppressWarnings("unchecked")
    public static <E> HashMultiset<E> hash(Collection<? extends E>... collections) {
        return hash(Arrays.asList(collections));
    }

    /**
     * Creates a new {@code HashMultiset} with contents taken from a
     * series of collections.
     *
     * @param <E> the type of element to be stored in the multiset.
     *
     * @param collections the collections to populate the new multiset.
     *
     * @return a new {@code HashMultiset} containing all items in the
     * given collections.
     */
    public static <E> HashMultiset<E> hash(Iterable<? extends Collection<? extends E>> collections) {
        HashMultiset<E> set = HashMultiset.create();
        CollectionUtil.addAll(set, collections);

        return set;
    }

    /**
     * Returns an object factory that will create empty {@code HashMultiset} instances.
     *
     * @param <E> the type of element to be stored in the multiset.
     *
     * @return an object factory that will create empty {@code HashMultiset} instances.
     */
    public static <E> ObjectFactory<HashMultiset<E>> hashFactory() {
        return new HashFactory<E>();
    }

    private static final class HashFactory<E> implements ObjectFactory<HashMultiset<E>> {
        @Override public HashMultiset<E> newInstance() {
            return HashMultiset.create();
        }
    }

    /**
     * Counts the number of objects in a multiset with a fixed number
     * of occurrences (the target tally).
     *
     * @param set the set to tally.
     *
     * @param target the number of occurrences to count.
     *
     * @return the number of objects in the input set containing
     * exactly {@code target} occurrences.
     */
    public static int tallyCount(Multiset<?> set, int target) {
        int tally = 0;

        for (Object key : set.elementSet())
            if (set.count(key) == target)
                ++tally;

        return tally;
    }

    /**
     * Creates a new {@code TreeMultiset} populated by a sequence of
     * integers.
     *
     * @param values the values to populate the new multiset.
     *
     * @return a new {@code TreeMultiset} counting the given integers.
     */
    public static TreeMultiset<Integer> tree(int... values) {
        TreeMultiset<Integer> set = TreeMultiset.create();

        for (int value : values)
            set.add(value);

        return set;
    }

    /**
     * Creates a new {@code TreeMultiset} with contents taken from a
     * series of collections.
     *
     * @param <E> the type of element to be stored in the multiset.
     *
     * @param collections the collections to populate the new multiset.
     *
     * @return a new {@code TreeMultiset} containing all items in the
     * given collections.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Comparable> TreeMultiset<E> tree(Collection<? extends E>... collections) {
        return tree(Arrays.asList(collections));
    }

    /**
     * Creates a new {@code TreeMultiset} with contents taken from a
     * series of collections.
     *
     * @param <E> the type of element to be stored in the multiset.
     *
     * @param collections the collections to populate the new multiset.
     *
     * @return a new {@code TreeMultiset} containing all items in the
     * given collections.
     */
    public static <E extends Comparable> TreeMultiset<E> tree(Iterable<? extends Collection<? extends E>> collections) {
        TreeMultiset<E> set = TreeMultiset.create();
        CollectionUtil.addAll(set, collections);

        return set;
    }

    /**
     * Returns an object factory that will create empty {@code TreeMultiset} instances.
     *
     * @param <E> the type of element to be stored in the multiset.
     *
     * @return an object factory that will create empty {@code TreeMultiset} instances.
     */
    public static <E extends Comparable> ObjectFactory<TreeMultiset<E>> treeFactory() {
        return new TreeFactory<E>();
    }

    private static final class TreeFactory<E extends Comparable> implements ObjectFactory<TreeMultiset<E>> {
        @Override public TreeMultiset<E> newInstance() {
            return TreeMultiset.create();
        }
    }
}
