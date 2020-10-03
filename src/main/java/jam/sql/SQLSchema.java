
package jam.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.lang.JamException;
import jam.report.LineBuilder;
import jam.util.FixedList;
import jam.stream.JamStreams;

/**
 * Builds the schema of a SQL table from its individual columns.
 */
public final class SQLSchema {
    private final String tableName;
    private final boolean hasPrimaryKey;
    private final boolean hasCompositeKey;
    private final List<SQLColumn> allColumns;
    private final List<SQLColumn> keyColumns;
    private final List<SQLColumn> dataColumns;

    private SQLSchema(String tableName, List<SQLColumn> columns) {
        this.tableName = tableName;

        this.allColumns = Collections.unmodifiableList(new ArrayList<SQLColumn>(columns));
        this.keyColumns = JamStreams.filter(columns, column -> column.isKey());
        this.dataColumns = JamStreams.filter(columns, column -> !column.isKey());
        
        this.hasPrimaryKey = hasPrimaryKey(keyColumns);
        this.hasCompositeKey = hasCompositeKey(keyColumns);

        validate();
    }

    private static boolean hasPrimaryKey(List<SQLColumn> columns) {
        int keyCount = (int) JamStreams.count(columns, column -> column.isPrimaryKey());

        switch (keyCount) {
        case 0:
            return false;

        case 1:
            return true;

        default:
            throw JamException.runtime("Duplicate primary keys.");
        }
    }

    private boolean hasCompositeKey(List<SQLColumn> columns) {
        int keyCount = (int) JamStreams.count(columns, column -> column.isCompositeKey());

        switch (keyCount) {
        case 0:
            return false;

        case 1:
            throw JamException.runtime("Only one composite key was defined.");

        default:
            return true;
        }
    }

    private void validate() {
        if (allColumns.isEmpty())
            throw JamException.runtime("No columns were defined.");

        if (hasPrimaryKey && hasCompositeKey)
            throw JamException.runtime("Both a primary and composite key were defined.");
    }

    /**
     * Creates a new schema from the table column descriptions.
     *
     * @param tableName the name of the SQL table.
     *
     * @param columns the table column descriptions.
     *
     * @return the table schema defined by the column descriptions.
     */
    public static SQLSchema create(String tableName, SQLColumn... columns) {
        return create(tableName, List.of(columns));
    }

    /**
     * Creates a new schema from the table column descriptions.
     *
     * @param tableName the name of the SQL table.
     *
     * @param columns the table column descriptions.
     *
     * @return the table schema defined by the column descriptions.
     */
    public static SQLSchema create(String tableName, List<SQLColumn> columns) {
        return new SQLSchema(tableName, columns);
    }

    /**
     * Constructs the {@code CREATE TABLE} command for this schema.
     *
     * @param engine the database engine that will create the table.
     *
     * @return the {@code CREATE TABLE} command for this schema.
     */
    public String formatCreateTable(SQLEngine engine) {
        LineBuilder builder = new LineBuilder(", ");

        for (SQLColumn column : allColumns)
            builder.append(column.join(engine));

        if (hasCompositeKey())
            builder.append(formatCompositeKeyConstraint());

        return String.format("CREATE TABLE IF NOT EXISTS %s (%s)", tableName, builder.toString());
    }

    private String formatCompositeKeyConstraint() {
        return String.format("CONSTRAINT %s_pkey PRIMARY KEY (%s)", tableName, joinCompositeKeys());
    }

    private String joinCompositeKeys() {
        LineBuilder builder = new LineBuilder(", ");

        for (SQLColumn column : keyColumns)
            if (column.isCompositeKey())
                builder.append(column.getName());

        return builder.toString();
    }

    /**
     * Constructs the {@code CREATE INDEX} commands for this schema.
     *
     * @return the {@code CREATE INDEX} commands for this schema.
     */
    public List<String> formatCreateIndex() {
        List<String> commands = new ArrayList<String>();

        for (SQLColumn column : allColumns)
            if (column.hasIndex())
                commands.add(String.format("CREATE INDEX IF NOT EXISTS %s_%s_idx ON %s (%s)",
                                           tableName, column.getName(), tableName, column.getName()));

        return commands;
    }

    /**
     * Returns the meta-data for all columns in this schema.
     *
     * @return the meta-data for all columns in this schema.
     */
    public int countColumns() {
        return allColumns.size();
    }

    /**
     * Returns the meta-data for the key columns in this schema.
     *
     * @return the meta-data for the key columns in this schema.
     */
    public int countKeyColumns() {
        return keyColumns.size();
    }

    /**
     * Returns the meta-data for the data columns in this schema.
     *
     * @return the meta-data for the data columns in this schema.
     */
    public int countDataColumns() {
        return dataColumns.size();
    }

    /**
     * Returns the meta-data for all columns in this schema.
     *
     * @return the meta-data for all columns in this schema.
     */
    public List<SQLColumn> getColumns() {
        return allColumns;
    }

    /**
     * Returns the meta-data for the key columns in this schema.
     *
     * @return the meta-data for the key columns in this schema.
     */
    public List<SQLColumn> getKeyColumns() {
        return keyColumns;
    }

    /**
     * Returns the meta-data for the data columns in this schema.
     *
     * @return the meta-data for the data columns in this schema.
     */
    public List<SQLColumn> getDataColumns() {
        return dataColumns;
    }

    /**
     * Returns the name of the table defined by this schema.
     *
     * @return the name of the table defined by this schema.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Identifies tables with a composite key.
     *
     * @return {@code true} iff this schema has a composite key.
     */
    public boolean hasCompositeKey() {
        return hasCompositeKey;
    }

    /**
     * Identifies tables with a single primary key.
     *
     * @return {@code true} iff this schema has a single primary key.
     */
    public boolean hasPrimaryKey() {
        return hasPrimaryKey;
    }
}
