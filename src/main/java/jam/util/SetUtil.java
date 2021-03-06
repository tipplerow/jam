
package jam.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import jam.lang.ObjectUtil;

/**
 * Provides utility operations on sets.
 */
public final class SetUtil {
    private SetUtil() {}

    /**
     * Adds elements to an existing set.
     *
     * @param <E> the element type.
     *
     * @param set the set in which to place the elements.
     *
     * @param elements elements to place in the set.
     */
    @SuppressWarnings("unchecked") 
    public static <E> void addAll(Set<E> set, E... elements) {
        for (E element : elements)
            set.add(element);
    }

    /**
     * Clones a set without knowing its runtime type.
     *
     * <p>The set must be an instance of a subclass defined in the
     * {@code java.util} package.
     *
     * @param <E> the element type.
     *
     * @param set the set to clone.
     *
     * @return a shallow copy of the input set.
     *
     * @throws IllegalArgumentException unless the set is an instance
     * of a subclass defined in the {@code java.util} package.
     */
    @SuppressWarnings("unchecked")
    public static <E> Set<E> clone(Set<E> set) {
        if (set instanceof HashSet)
            return (Set<E>) ((HashSet<E>) set).clone();

        if (set instanceof TreeSet)
            return new TreeSet((TreeSet<E>) set);

        if (set instanceof EnumSet)
            return ((EnumSet) set).clone();

        throw new IllegalArgumentException("Unsupported Set implementation.");
    }

    /**
     * Counts the number of elements shared by two sets (the size of
     * their intersection).
     *
     * @param s1 the first set.
     *
     * @param s2 the second set.
     *
     * @return the number of elements shared by the two sets (the size
     * of their intersection).
     */
    public static int countShared(Set<?> s1, Set<?> s2) {
        //
        // More efficient to iterate over the smaller set...
        //
        Set<?> larger;
        Set<?> smaller;

        if (s1.size() > s2.size()) {
            larger  = s1;
            smaller = s2;
        }
        else {
            larger  = s2;
            smaller = s1;
        }

        int result = 0;

        for (Object obj : smaller)
            if (larger.contains(obj))
                ++result;

        return result;
    }

    /**
     * Counts the number of unique elements bewteen two sets (the size
     * of their union).
     *
     * @param s1 the first set.
     *
     * @param s2 the second set.
     *
     * @return the number of unique elements bewteen the two sets (the
     * size of their union).
     *
     * @throws IllegalStateException if the size of the union exceeds
     * {@code Integer.MAX_VALUE}.
     */
    public static int countUnique(Set<?> s1, Set<?> s2) {
        //
        // More efficient to iterate over the smaller set...
        //
        Set<?> larger;
        Set<?> smaller;

        if (s1.size() > s2.size()) {
            larger  = s1;
            smaller = s2;
        }
        else {
            larger  = s2;
            smaller = s1;
        }

        long result = larger.size();

        for (Object obj : smaller)
            if (!larger.contains(obj))
                ++result;

        if (result <= Integer.MAX_VALUE)
            return (int) result;
        else
            throw new IllegalStateException("Integer overflow.");
    }

    /**
     * Generates the difference of a set and another collection; the
     * input objects are unchanged.
     *
     * @param <E> the element type.
     *
     * @param set the original set.
     *
     * @param coll another collection.
     *
     * @return a new set with the same runtime type as {@code set}
     * containing only the elements from {@code set} that are not 
     * also contained in {@code coll}.
     */
    public static <E> Set<E> difference(Set<E> set, Collection<? extends E> coll) {
        Set<E> result = clone(set);
        result.removeAll(coll);
        return result;
    }

    /**
     * Returns an immutable set with an implementation that is
     * optimized for the number of elements.
     *
     * @param <E> the element type.
     *
     * @param elements the elements to add to the set.
     *
     * @return an optimized immutable set for the given elements.
     */
    public static <E> Set<E> fixed(Collection<E> elements) {
        if (elements.isEmpty())
            return Collections.emptySet();

        Set<E> set =
            elements.size() < 2 ? new TreeSet<E>() : new HashSet<E>(elements.size(), 1.0f);

        set.addAll(elements);
        return Collections.unmodifiableSet(set);
    }

    /**
     * Generates the intersection of a set and another collection; the
     * input objects are unchanged.
     *
     * @param <E> the element type.
     *
     * @param set the original set.
     *
     * @param coll another collection.
     *
     * @return a new set with the same runtime type as {@code set}
     * containing only the elements from {@code set} that are also
     * contained in {@code coll}.
     */
    public static <E> Set<E> intersection(Set<E> set, Collection<? extends E> coll) {
        Set<E> result = clone(set);
        result.retainAll(coll);
        return result;
    }

    /**
     * Creates an enum set with initial elements.
     *
     * @param <E> the element type.
     *
     * @param elementType the class object of the element type for the
     * enum set.
     *
     * @param elements elements to place in the set.
     *
     * @return a new {@code EnumSet} containing the specified
     * elements.
     */
    @SuppressWarnings("unchecked") 
    public static <E extends Enum<E>> EnumSet<E> newEnumSet(Class<E> elementType, E... elements) {
	EnumSet<E> set = EnumSet.noneOf(elementType);

	addAll(set, elements);
	return set;
    }

    /**
     * Finds objects that are missing from (not contained in) an
     * existing set.
     *
     * @param <K> the element type.
     *
     * @param present the set of existing objects (they are present).
     *
     * @param requested the objects that are requested to be present.
     *
     * @return a new set containing all requested objects that are not
     * present in the set.
     */
    public static <K> Set<K> missing(Set<K> present, Collection<K> requested) {
        Set<K> missing = new HashSet<K>();

        for (K key : requested)
            if (!present.contains(key))
                missing.add(key);

        return missing;
    }

    /**
     * Creates a hash set with initial elements.
     *
     * @param <E> the element type.
     *
     * @param elements elements to place in the set.
     *
     * @return a new {@code HashSet} containing the specified
     * elements.
     */
    @SuppressWarnings("unchecked") 
    public static <E> HashSet<E> newHashSet(E... elements) {
	HashSet<E> set = new HashSet<E>(elements.length);

	addAll(set, elements);
	return set;
    }

    /**
     * Creates a hash set by applying a function (or lambda
     * expression) to every element in a collection.
     *
     * @param <T> element type for the source collection.
     *
     * @param <R> element type for the result set.
     *
     * @param source the collection to process.
     *
     * @param function the function (or lambda expression) to apply.
     *
     * @return a new hash set containing the results of each function
     * applied to the elements in the collection.
     */
    public static <T, R> HashSet<R> newHashSet(Collection<T> source, Function<T, R> function) {
        return newHashSet(source.iterator(), function);
    }

    /**
     * Creates a hash set by applying a function (or lambda
     * expression) to every element in an iteration.
     *
     * @param <T> element type for the source iterator.
     *
     * @param <R> element type for the result set.
     *
     * @param source the iterator to process.
     *
     * @param function the function (or lambda expression) to apply.
     *
     * @return a new hash set containing the results of each function
     * applied to the elements in the iteration.
     */
    public static <T, R> HashSet<R> newHashSet(Iterator<T> source, Function<T, R> function) {
        HashSet<R> result = new HashSet<R>();

        while (source.hasNext())
            result.add(function.apply(source.next()));

        return result;
    }

    /**
     * Creates a tree set with initial elements.
     *
     * @param <E> the element type.
     *
     * @param elements elements to place in the set.
     *
     * @return a new {@code TreeSet} containing the specified
     * elements.
     */
    @SuppressWarnings("unchecked") 
    public static <E> TreeSet<E> newTreeSet(E... elements) {
	TreeSet<E> set = new TreeSet<E>();

	addAll(set, elements);
	return set;
    }

    /**
     * Creates a tree set with a specific comparator and initial
     * elements.
     *
     * @param <E> the element type.
     *
     * @param comparator the element comparator.
     *
     * @param elements elements to place in the set.
     *
     * @return a new {@code TreeSet} containing the specified
     * elements ordered by the specified comparator.
     */
    @SuppressWarnings("unchecked") 
    public static <E> TreeSet<E> newTreeSet(Comparator<E> comparator, E... elements) {
	TreeSet<E> set = new TreeSet<E>(comparator);

	addAll(set, elements);
	return set;
    }

    /**
     * Generates the union of a set with another collection; the input
     * objects are unchanged.
     *
     * @param <E> the element type.
     *
     * @param set the original set.
     *
     * @param coll another collection.
     *
     * @return a new set with the same runtime type as {@code set}
     * containing all unique elements from {@code set} and {@code coll}.
     */
    public static <E> Set<E> union(Set<E> set, Collection<? extends E> coll) {
        Set<E> result = clone(set);
        result.addAll(coll);
        return result;
    }
}
