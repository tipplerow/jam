
package jam.lang;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Provides an abstraction for object creation.
 */
public interface ObjectFactory<T> {
    /**
     * Creates a new object.
     *
     * @return the new object.
     */
    public abstract T newInstance();

    /**
     * Creates new objects.
     *
     * @param count the number of new objects to create.
     *
     * @return the new objects.
     */
    public default Collection<T> newInstances(int count) {
        Collection<T> instances = new ArrayList<T>(count);
        addInstances(instances, count);
        return instances;
    }

    /**
     * Adds new objects to a collection until reaching a desired size.
     *
     * @param instances the collection to hold the new objects.
     *
     * @param finalSize the final size of the collection.
     *
     * @throws IllegalArgumentException if the final size exceeds the
     * initial size of the collection.
     */
    public default void addInstances(Collection<T> instances, int finalSize) {
        if (finalSize < instances.size())
            throw new IllegalArgumentException("Collection size exceeds the final size.");

        while (instances.size() < finalSize)
            instances.add(newInstance());
    }

    /**
     * Returns an object factory that will create new instances of a
     * given class by calling the default constructor for the class.
     *
     * @param <T> the runtime object type.
     *
     * @param theClass the class of objects to create.
     *
     * @return an object factory that will create new instances of the
     * specified class.
     *
     * @throws IllegalArgumentException if the specified class does
     * not define an accessible default constructor.
     */
    public static <T> ObjectFactory<T> forClass(Class<T> theClass) {
        return new ClassFactory<T>(theClass);
    }

    /**
     * Returns an object factory that will generate a sequence of
     * {@code Integer} objects with values starting at {@code 0} and
     * increasing by one with each with call to {@code newInstance()}.
     *
     * @return an object factory that will generate a sequence of
     * {@code Integer} objects.
     */
    public static ObjectFactory<Integer> forInteger() {
        return new IntegerFactory();
    }

    /**
     * Returns an object factory that will always return {@code null}
     * references.
     *
     * @param <T> the runtime object type.
     *
     * @return an object factory that will always return {@code null}
     * references.
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectFactory<T> forNull() {
        return (ObjectFactory<T>) NullFactory.INSTANCE;
    }

    /**
     * Returns an object factory that will create new instances of the
     * same runtime type as ("like") a template object by calling the
     * default constructor for the class.
     *
     * <p>If the template is {@code null}, then all new instances will
     * be {@code null} as well.
     *
     * @param <T> the runtime object type.
     *
     * @param template the template object defining the runtime type
     * of objects created by the factory, or {@code null} for a
     * factory that always generates {@code null} instances.
     *
     * @return an object factory that will create new instances of the
     * same runtime type as ("like") the specified template object.
     *
     * @throws IllegalArgumentException if the template class does not
     * define an accessible default constructor.
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectFactory<T> like(T template) {
        if (template == null)
            return forNull();
        else
            return forClass((Class<T>) template.getClass());
    }
}

final class ClassFactory<T> implements ObjectFactory<T> {
    private final Class<T> theClass;

    ClassFactory(Class<T> theClass) {
        if (!ObjectUtil.hasDefaultConstructor(theClass))
            throw new IllegalArgumentException(String.format("No accessible default constructor for [%s].", theClass));

        this.theClass = theClass;
    }

    @Override public T newInstance() {
        return ObjectUtil.newInstance(theClass);
    }
}

final class IntegerFactory implements ObjectFactory<Integer> {
    private int nextInt = 0;

    IntegerFactory() {}

    @Override public Integer newInstance() {
        return Integer.valueOf(nextInt++);
    }
}

final class NullFactory implements ObjectFactory<Object> {
    private NullFactory() {
    }

    static final NullFactory INSTANCE = new NullFactory();

    @Override public Object newInstance() {
        return null;
    }
}
