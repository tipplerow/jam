
package jam.sql;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import jam.report.LineBuilder;
import jam.util.ListUtil;

/**
 * Encapsulates SQL schema meta-data for a database column.
 */
public final class SQLColumn {
    private final String name;
    private final String type;
    private final Set<Qualifier> qualifiers;

    private enum Qualifier {
        COMPOSITE_KEY(null),
        NOT_NULL("NOT NULL"),
        PRIMARY_KEY("PRIMARY KEY"),
        UNIQUE("UNIQUE"),
        WITH_INDEX(null);

        private final String columnConstraintString;

        private Qualifier(String columnConstraintString) {
            this.columnConstraintString = columnConstraintString;
        }

        public String getColumnConstraintString() {
            return columnConstraintString;
        }

        public boolean isColumnConstraint() {
            return columnConstraintString != null;
        }
    }

    private SQLColumn(String name, String type, Set<Qualifier> qualifiers) {
        this.name = name;
        this.type = type;
        this.qualifiers = qualifiers;
    }

    /**
     * Creates a new column with a fixed name and data type.
     *
     * @param name the name for the column.
     *
     * @param type the data type for the column.
     *
     * @return the SQL column meta-data.
     */
    public static SQLColumn create(String name, String type) {
        return new SQLColumn(name, type, EnumSet.noneOf(Qualifier.class));
    }

    /**
     * Extracts the names from a list of columns.
     *
     * @param columns the table columns.
     *
     * @return the names of the given columns (in the same order).
     */
    public static List<String> getNames(List<SQLColumn> columns) {
        return ListUtil.apply(columns, x -> x.getName());
    }

    private SQLColumn add(Qualifier... qualifierList) {
        SQLColumn newColumn =
            new SQLColumn(name, type, EnumSet.copyOf(qualifiers));

        for (Qualifier qualifier : qualifierList)
            newColumn.qualifiers.add(qualifier);

        return newColumn;
    }

    /**
     * Joins the name, data type, and qualifiers for this column into
     * a single schema string.
     *
     * @return a single string that describes the name, data type, and
     * qualifiers for this column.
     */
    public String join() {
        LineBuilder builder = new LineBuilder(" ");

        builder.append(name);
        builder.append(type);

        for (Qualifier qualifier : qualifiers)
            if (qualifier.isColumnConstraint())
                builder.append(qualifier.getColumnConstraintString());

        return builder.toString();
    }

    /**
     * Creates a new column like this column as a composite key for
     * its table and adds a non-unique index for the column; this
     * column is unchanged.
     *
     * @return the new column description.
     *
     * @throws IllegalStateException if this column has any other
     * qualifiers.
     */
    public SQLColumn compositeKey() {
        if (!qualifiers.isEmpty())
            throw new IllegalStateException("No other qualifiers are allowed.");

        return add(Qualifier.COMPOSITE_KEY, Qualifier.WITH_INDEX);
    }

    /**
     * Creates a new column like this column as the primary key for
     * its table; this column is unchanged.
     *
     * @return the new column description.
     *
     * @throws IllegalStateException if this column has any other
     * qualifiers.
     */
    public SQLColumn primaryKey() {
        if (!qualifiers.isEmpty())
            throw new IllegalStateException("PRIMARY KEY must be the only column qualifier.");

        return add(Qualifier.PRIMARY_KEY);
    }

    /**
     * Creates a new column like this column with a uniqueness
     * constraint; this column is unchanged.
     *
     * @return the new column description.
     *
     * @throws IllegalStateException if this column is a primary key
     * column or has an explicit index.
     */
    public SQLColumn unique() {
        if (isPrimaryKey())
            throw new IllegalStateException("Primary keys are already unique.");

        if (hasIndex())
            throw new IllegalStateException("A uniqueness constraint would add a redundant index.");

        return add(Qualifier.UNIQUE);
    }

    /**
     * Creates a new column like this column with a non-null
     * constraint; this column is unchanged.
     *
     * @return the new column description.
     */
    public SQLColumn notNull() {
        if (isPrimaryKey())
            throw new IllegalStateException("Primary keys are already non-null.");

        return add(Qualifier.NOT_NULL);
    }

    /**
     * Creates a new column like this column with an index
     * constraint; this column is unchanged.
     *
     * @return the new column description.
     *
     * @throws IllegalStateException if this column is a primary key
     * or has been marked as unique (has an implicit index).
     */
    public SQLColumn withIndex() {
        if (isPrimaryKey())
            throw new IllegalStateException("Primary keys are already indexed.");

        if (isUnique())
            throw new IllegalStateException("Unique columns are already indexed.");

        if (hasIndex())
            throw new IllegalStateException("This column is already indexed.");

        return add(Qualifier.WITH_INDEX);
    }

    /**
     * Returns the name of this column.
     *
     * @return the name of this column.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of this column.
     *
     * @return the type of this column.
     */
    public String getType() {
        return type;
    }

    /**
     * Identifies composite key columns.
     *
     * @return {@code true} iff this column is a component of the
     * composite key for its table.
     */
    public boolean isCompositeKey() {
        return qualifiers.contains(Qualifier.COMPOSITE_KEY);
    }

    /**
     * Identifies primary key columns.
     *
     * @return {@code true} iff this column is the primary key for its
     * table.
     */
    public boolean isPrimaryKey() {
        return qualifiers.contains(Qualifier.PRIMARY_KEY);
    }

    /**
     * Identifies unique (but not primary key) columns.
     *
     * @return {@code true} iff this column requires unique values but
     * is not the primary key column.
     */
    public boolean isUnique() {
        return qualifiers.contains(Qualifier.UNIQUE);
    }

    /**
     * Identifies non-null columns.
     *
     * @return {@code true} iff this column forbids {@code NULL}
     * values.
     */
    public boolean isNotNull() {
        return qualifiers.contains(Qualifier.NOT_NULL);
    }

    /**
     * Identifies indexed (but not unique) columns.
     *
     * @return {@code true} iff this column has a table index.
     */
    public boolean hasIndex() {
        return qualifiers.contains(Qualifier.WITH_INDEX);
    }
}
