
package jam.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembles SQL queries piece by piece.
 */
public final class QueryBuilder {
    private final List<String> selectWhat = new ArrayList<String>();
    private final List<String> fromTables = new ArrayList<String>();
    private final List<String> innerJoins = new ArrayList<String>();
    private final List<String> whereConds = new ArrayList<String>();
    private final List<String> groupBy = new ArrayList<String>();
    private final List<String> orderBy = new ArrayList<String>();

    private QueryBuilder() {
    }

    /**
     * Creates a new empty query builder.
     *
     * @return a new empty query builder.
     */
    public static QueryBuilder create() {
        return new QueryBuilder();
    }

    /**
     * Determines whether this query builder is in a valid state: at
     * least one {@code FROM} table must be specified and at least one
     * item must be selected.
     *
     * @return {@code true} iff this query builder defines a valid SQL
     * query.
     */
    public boolean isValid() {
        return !selectWhat.isEmpty() && !fromTables.isEmpty();
    }

    /**
     * Adds a table to the {@code FROM} list.
     *
     * @param table the table to select from.
     */
    public void from(String table) {
        fromTables.add(table);
    }

    /**
     * Adds a {@code GROUP BY} clause to this query.
     *
     * @param by the column to group by.
     */
    public void groupBy(String by) {
        groupBy.add(by);
    }

    /**
     * Adds an {@code INNER JOIN} condition to this query:
     *
     * <pre>
     *    INNER JOIN table ON column1 = column2
     * </pre>
     *
     * @param table the table to be joined.
     *
     * @param column1 the first column in the join equality.
     *
     * @param column2 the first column in the join equality.
     */
    public void innerJoin(String table, String column1, String column2) {
        innerJoins.add(String.format(" INNER JOIN %s ON %s = %s", table, column1, column2));
    }

    /**
     * Adds an ascending {@code ORDER BY} clause to this query.
     *
     * @param by the column to order by.
     */
    public void orderBy(String by) {
        orderBy.add(by);
    }

    /**
     * Adds a descending {@code ORDER BY} clause to this query.
     *
     * @param by the column to order by.
     */
    public void orderByDesc(String by) {
        orderBy.add(by + " DESC");
    }

    /**
     * Adds an item to the {@code SELECT} list.
     *
     * @param what the item to select.
     */
    public void select(String what) {
        selectWhat.add(what);
    }

    /**
     * Ensures that this query is valid.
     *
     * @throws IllegalStateException unless this query is in a valid
     * state.
     */
    public void validate() {
        if (!isValid())
            throw new IllegalStateException("Invalid query state.");
    }

    /**
     * Adds a {@code WHERE} condition to this query.
     *
     * @param cond the condition to add.
     */
    public void where(String cond) {
        whereConds.add(cond);
    }

    @Override public String toString() {
        validate();

        StringBuilder builder = new StringBuilder();

        builder.append("SELECT ");
        builder.append(String.join(", ", selectWhat));
        builder.append(" FROM ");
        builder.append(String.join(", ", fromTables));

        if (!innerJoins.isEmpty())
            builder.append(String.join("", innerJoins));

        if (!whereConds.isEmpty()) {
            builder.append(" WHERE ");
            builder.append(String.join(" AND ", whereConds));
        }

        if (!groupBy.isEmpty()) {
            builder.append(" GROUP BY ");
            builder.append(String.join(", ", groupBy));
        }

        if (!orderBy.isEmpty()) {
            builder.append(" ORDER BY ");
            builder.append(String.join(", ", orderBy));
        }

        return builder.toString();
    }
}
