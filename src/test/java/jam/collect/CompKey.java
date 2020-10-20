
package jam.collect;

import java.time.LocalDate;

public final class CompKey extends JoinKey<String, LocalDate> {
    public CompKey(String name, LocalDate date) {
        super(name, date);
    }

    public String getName() {
        return key1;
    }

    public LocalDate getDate() {
        return key2;
    }
}
