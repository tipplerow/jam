
package jam.collect;

import java.time.LocalDate;

public final class CompRecord {
    public final CompKey key;
    public final int ival;

    public CompRecord(CompKey key, int ival) {
        this.key = key;
        this.ival = ival;
    }

    public CompRecord(String name, LocalDate date, int ival) {
        this(new CompKey(name, date), ival);
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof CompRecord) && equalsRecord((CompRecord) obj);
    }

    private boolean equalsRecord(CompRecord that) {
        return this.key.equals(that.key) && this.ival == that.ival;
    }

    @Override public String toString() {
        return String.format("CompRecord(name = '%s', date = '%s', ival = %d)",
                             key.getName(), key.getDate(), ival);
    }
}
