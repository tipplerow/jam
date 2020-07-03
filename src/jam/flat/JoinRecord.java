
package jam.flat;

/**
 * A database record with a single primary key that may be written to
 * a delimited flat file.
 *
 * @param <K1> the runtime type of the primary key.
 *
 * @param <K2> the runtime type of the foreign key.
 */
public interface JoinRecord<K1, K2> extends FlatRecord<K1> {
    /**
     * Returns the foreign key for this record.
     *
     * @return the foreign key for this record.
     */
    public abstract K2 getForeignKey();
}
