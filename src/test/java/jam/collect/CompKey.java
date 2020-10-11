
package jam.collect;

import java.time.LocalDate;

public final class CompKey {
    public final String name;
    public final LocalDate date;

    public CompKey(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof CompKey) && equalsKey((CompKey) obj);
    }

    private boolean equalsKey(CompKey that) {
        return this.name.equals(that.name)
            && this.date.equals(that.date);
    }

    @Override public int hashCode() {
        return name.hashCode() + 37 * date.hashCode();
    }

    @Override public String toString() {
        return String.format("CompKey(name = '%s', date = '%s')", name, date);
    }
}
