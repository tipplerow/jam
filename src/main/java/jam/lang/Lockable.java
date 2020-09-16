
package jam.lang;

/**
 * Implements an object whose value may be <em>locked</em> against
 * further change.
 *
 * <p>A lockable object is created in an unlocked state, in which its
 * value may be changed any number of times via {@code set(E value)}.
 * After calling the {@code lock()} method, however, any subsequent
 * calls to {@code set(E value)} will trigger an exception.  Note that
 * there is no "unlock" method; once locked, the object remains locked.
 *
 * <p>This class is intended for situations where an immutable value
 * is desired, but the value is not known at the time the object is
 * created, e.g., for computational results created on-demand via lazy
 * initialization.
 *
 * @param <E> the type of lockable object.
 */
public final class Lockable<E> {
    private E value;
    private boolean locked = false;

    /**
     * Creates a new unset and unlocked object.
     */
    public Lockable() {
        this.value = null;
    }

    /**
     * Creates a new (unlocked) object with a given value.
     *
     * @param value the initial value.
     */
    public Lockable(E value) {
        this.value = value;
    }

    /**
     * Creates a new unset and unlocked object.
     *
     * @param <E> the underlying type.
     *
     * @return the new lockable object.
     */
    public static <E> Lockable<E> create() {
        return new Lockable<E>();
    }

    /**
     * Creates a new (unlocked) object with a given value.
     *
     * @param <E> the underlying type.
     *
     * @param value the initial value.
     *
     * @return the new lockable object.
     */
    public static <E> Lockable<E> create(E value) {
        return new Lockable<E>(value);
    }

    /**
     * Returns the underlying value in this lockable object.
     *
     * @return the underlying value in this lockable object.
     */
    public E get() {
        return value;
    }

    /**
     * Identifies locked objects.
     *
     * @return {@code true} iff this object has been locked.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Identifies objects that have been assigned non-{@code null}
     * values.
     *
     * @return {@code true} iff this object has a non-{@code null}
     * value.
     */
    public boolean isSet() {
        return value != null;
    }

    /**
     * Identifies unlocked objects.
     *
     * @return {@code true} iff this object is unlocked.
     */
    public boolean isUnlocked() {
        return !isLocked();
    }

    /**
     * Identifies unset objects.
     *
     * @return {@code true} iff this object is unset.
     */
    public boolean isUnset() {
        return !isSet();
    }

    /**
     * Locks this object against changes in its value.
     *
     * <p>Note that there is no "unlock" method; once locked, an
     * object remains locked for the rest of its existence.
     *
     * <p>No exception will be triggered if this object is already
     * locked; the method call will just have no further effect.
     */
    public void lock() {
        locked = true;
    }

    /**
     * Assigns (or reassigns) a value to this object.
     *
     * @param value the value to assign.
     *
     * @throws IllegalStateException if this object is locked, even if
     * the input value is equal to the current value.
     */
    public void set(E value) {
        if (isLocked())
            throw new IllegalStateException("Attempting to change a locked object.");

        this.value = value;
    }

    /**
     * Unsets the value of this object (assigns a {@code null} value).
     *
     * @throws IllegalStateException if this object is locked, even if
     * it already unset.
     */
    public void unset() {
        set(null);
    }
}
