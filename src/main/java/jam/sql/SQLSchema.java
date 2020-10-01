
package jam.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.lang.JamException;
import jam.report.LineBuilder;
import jam.util.FixedList;

/**
 * Builds the schema of a SQL table from its individual columns.
 */
public final class SQLSchema {
    private final String tableName;
    private final List<SQLColumn> columns;

    private SQLSchema(String tableName, List<SQLColumn> columns) {
        this.tableName = tableName;
        this.columns = Collections.unmodifiableList(new ArrayList<SQLColumn>(columns));

        validateColumns();
    }

    private void validateColumns() {
        int primaryKeyCount = countPrimaryKeys();

        if (primaryKeyCount == 0)
            validateCompositeKeys();
        else if (primaryKeyCount > 1)
            throw JamException.runtime("Duplicate primary keys.");
    }

    private int countPrimaryKeys() {
        int keyCount = 0;

        for (SQLColumn column : columns)
            if (column.isPrimaryKey())
                ++keyCount;

        return keyCount;
    }

    private void validateCompositeKeys() {
        //
        // Either zero or two or more composite keys are allowed...
        //
        int compositeKeyCount = countCompositeKeys();

        if (compositeKeyCount == 1)
            throw JamException.runtime("Only one composite key was defined.");
    }

    private int countCompositeKeys() {
        int keyCount = 0;

        for (SQLColumn column : columns)
            if (column.isCompositeKey())
                ++keyCount;

        return keyCount;
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
     * Creates the table and indexes described by this schema.
     *
     * @param db the database that will house the table.
     *
     * @throws RuntimeException if any database errors occur.
     */
    public void createTable(SQLDb db) {
        db.executeUpdate(formatCreateTable(db.getEngineType()));

        for (String createIndexCommand : formatCreateIndex())
            db.executeUpdate(createIndexCommand);
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

        for (SQLColumn column : columns)
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

        for (SQLColumn column : columns)
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

        for (SQLColumn column : columns)
            if (column.hasIndex())
                commands.add(String.format("CREATE INDEX IF NOT EXISTS %s_%s_idx ON %s (%s)",
                                           tableName, column.getName(), tableName, column.getName()));

        return commands;
    }

    /**
     * Identifies tables with a composite key.
     *
     * @return {@code true} iff this schema has a composite key.
     */
    public boolean hasCompositeKey() {
        return countCompositeKeys() > 1;
    }

    /**
     * Identifies tables with a single primary key.
     *
     * @return {@code true} iff this schema has a single primary key.
     */
    public boolean hasPrimaryKey() {
        return countPrimaryKeys() == 1;
    }
}
