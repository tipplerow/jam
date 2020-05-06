
package jam.sql;

import java.util.ArrayList;
import java.util.List;

import jam.util.StringUtil;

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
     * Adds a {@code GROUP BY} clause to this query.
     *
     * @param table the table containing the column to group by.
     *
     * @param column the column to group by.
     */
    public void groupBy(String table, String column) {
        groupBy(tableColumn(table, column));
    }

    private static String tableColumn(String table, String column) {
        return table + "." + column;
    }

    /**
     * Adds an {@code INNER JOIN} condition to this query:
     *
     * <pre>
     *    INNER JOIN joinTable ON joinTable.joinColumn = baseTable.baseCcolumn
     * </pre>
     *
     * @param joinTable the joining table.
     *
     * @param joinColumn the column in the join table to match.
     *
     * @param baseTable the base table.
     *
     * @param baseColumn the column in the base table to match.
     */
    public void innerJoin(String joinTable,
                          String joinColumn,
                          String baseTable,
                          String baseColumn) {
        innerJoins.add(String.format(" INNER JOIN %s ON %s = %s",
                                     joinTable,
                                     tableColumn(joinTable, joinColumn),
                                     tableColumn(baseTable, baseColumn)));
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
     * Adds an ascending {@code ORDER BY} clause to this query.
     *
     * @param table the table containing the column to order by.
     *
     * @param column the column to order by.
     */
    public void orderBy(String table, String column) {
        orderBy(tableColumn(table, column));
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
     * Adds a descending {@code ORDER BY} clause to this query.
     *
     * @param table the table containing the column to order by.
     *
     * @param column the column to order by.
     */
    public void orderByDesc(String table, String column) {
        orderByDesc(tableColumn(table, column));
    }

    /**
     * Adds an item to the {@code SELECT} list.
     *
     * @param what the column or item to select.
     */
    public void select(String what) {
        selectWhat.add(what);
    }

    /**
     * Adds a table column to the {@code SELECT} list.
     *
     * @param table the table to select from.
     *
     * @param column the column to select.
     */
    public void select(String table, String column) {
        select(tableColumn(table, column));
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

    /**
     * Adds a {@code WHERE} condition to this query.
     *
     * @param table the table in the {@code WHERE} condition.
     *
     * @param column the column in the {@code WHERE} condition.
     *
     * @param operator the operator in the {@code WHERE} condition.
     *
     * @param target the target in the {@code WHERE} condition.
     *
     * @param quote whether the target string must be enclosed in
     * single quotes.
     */
    public void where(String table, String column, String operator, String target, boolean quote) {
        where(whereClause(table, column, operator, target, quote));
    }

    private static String whereClause(String table, String column, String operator, String target, boolean quote) {
        if (quote)
            target = StringUtil.singleQuote(target);

        return tableColumn(table, column) + " " + operator + " " + target;
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
