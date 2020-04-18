
package jam.sql;

import java.util.Collections;
import java.util.List;

import jam.report.LineBuilder;
import jam.util.FixedList;
import jam.util.ListUtil;

/**
 * Encapsulates SQL schema meta-data for a database column.
 */
public final class SQLColumn {
    private final String name;
    private final String type;
    private final FixedList<String> qualifiers;

    private SQLColumn(String name, String type, FixedList<String> qualifiers) {
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
        return new SQLColumn(name, type, FixedList.empty());
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

    /**
     * Joins column descriptions to form a complete table schema
     * description.
     *
     * @param columns the table columns.
     *
     * @return the table schema.
     */
    public static String schema(List<SQLColumn> columns) {
        LineBuilder builder = new LineBuilder(", ");

        for (SQLColumn column : columns)
            builder.append(column.join());

        return builder.toString();
    }

    private SQLColumn addQualifier(String qualifier) {
        return new SQLColumn(name, type, qualifiers.append(qualifier));
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

        for (String qualifier : qualifiers)
            builder.append(qualifier);

        return builder.toString();
    }

    /**
     * Creates a new column like this column with a new foreign key
     * constraint; this column is unchanged.
     *
     * @param tableName the name of the referenced table.
     *
     * @param foreignKey the name of the key column in the referenced
     * table.
     *
     * @return the new column description.
     */
    public SQLColumn foreignKey(String tableName, String foreignKey) {
        return addQualifier(String.format("REFERENCES %s(%s)", tableName, foreignKey));
    }

    /**
     * Creates a new column like this column with a primary key
     * constraint; this column is unchanged.
     *
     * @return the new column description.
     */
    public SQLColumn primaryKey() {
        return addQualifier("PRIMARY KEY");
    }

    /**
     * Creates a new column like this column with a uniqueness
     * constraint; this column is unchanged.
     *
     * @return the new column description.
     */
    public SQLColumn unique() {
        return addQualifier("UNIQUE");
    }

    /**
     * Creates a new column like this column with a non-null
     * constraint; this column is unchanged.
     *
     * @return the new column description.
     */
    public SQLColumn notNull() {
        return addQualifier("NOT NULL");
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
     * Returns a read-only view of the qualifiers for this column.
     *
     * @return a read-only view of the qualifiers for this column.
     */
    public List<String> getQualifiers() {
        return Collections.unmodifiableList(qualifiers);
    }
}
