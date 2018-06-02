
package jam.bio;

import java.util.List;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;

import jam.util.RegexUtil;

/**
 * Encapsulates the complete lineage of ancestors for a propagating
 * entity.
 */
public final class AncestryRecord {
    private final LongList lineage;

    private AncestryRecord(LongList lineage) {
        validate(lineage);
        this.lineage = LongLists.unmodifiable(lineage);
    }

    private static void validate(LongList lineage) {
        if (lineage.size() < 1)
            throw new IllegalArgumentException("Ancestry must include at least one member.");
    }

    /**
     * Creates a new ancestry record for a given propagator.
     *
     * @param propagator the propagator to examine.
     *
     * @return the ancestry record for the given propagator.
     */
    public static AncestryRecord create(Propagator propagator) {
        List<? extends Propagator> ancestors = propagator.traceLineage();
        LongList lineage = new LongArrayList(ancestors.size());

        for (Propagator ancestor : ancestors)
            lineage.add(ancestor.getIndex());

        return new AncestryRecord(lineage);
    }

    /**
     * Creates a new record by parsing a line from an ancestry file.
     *
     * @param s the line to parse.
     *
     * @return the record defined by the input string.
     *
     * @throws IllegalArgumentException unless the input string is a
     * valid representation of an ancestry record.
     */
    public static AncestryRecord parse(String s) {
        String[] fields = RegexUtil.COMMA.split(s);

        if (fields.length < 1)
            throw new IllegalArgumentException("Invalid record: [" + s + "].");

        LongList lineage = new LongArrayList(fields.length);

        for (String field : fields)
            lineage.add(Long.parseLong(field.trim()));

        return new AncestryRecord(lineage);
    }

    /**
     * Formats this record for writing to an ancestry file.
     *
     * @return the canonical string representation for this record.
     */
    public String format() {
        StringBuilder builder = new StringBuilder();
        builder.append(lineage.getLong(0));

        for (int index = 1; index < lineage.size(); ++index) {
            builder.append(",");
            builder.append(lineage.getLong(index));
        }

        return builder.toString();
    }

    /**
     * Returns the index of the youngest propagator.
     *
     * @return the index of the youngest propagator.
     */
    public long getChildIndex() {
        return lineage.getLong(lineage.size() - 1);
    }

    /**
     * Returns the index of the founding propagator.
     *
     * @return the index of the founding propagator.
     */
    public long getFounderIndex() {
        return lineage.getLong(0);
    }

    /**
     * Returns a read-only view of the lineage described by this
     * record.
     *
     * @return a read-only view of the lineage described by this
     * record.
     */
    public LongList viewLineage() {
        return lineage;
    }

    @Override public boolean equals(Object that) {
        return (that instanceof AncestryRecord) && equalsRecord((AncestryRecord) that);
    }

    private boolean equalsRecord(AncestryRecord that) {
        return this.lineage.equals(that.lineage);
    }
}
