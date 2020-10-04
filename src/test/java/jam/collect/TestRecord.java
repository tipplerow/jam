
package jam.collect;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jam.lang.ObjectUtil;
import jam.math.DoubleComparator;

public final class TestRecord {
    public final String key;
    public final int ival;
    public final double dval;
    public final LocalDate date;
    public final LocalDateTime time;

    public TestRecord(String key, int ival, double dval, LocalDate date, LocalDateTime time) {
        this.key = key;
        this.ival = ival;
        this.dval = dval;
        this.date = date;
        this.time = time;
    }

    public static final TestRecord REC1 =
        new TestRecord("abc", 1, 1.1, LocalDate.parse("2020-01-02"), LocalDateTime.parse("2020-01-02T03:04:05"));

    public static final TestRecord REC2 =
        new TestRecord("def", 2, 2.2, LocalDate.parse("2020-02-03"), LocalDateTime.parse("2020-02-03T04:05:06"));

    public static final TestRecord REC3 =
        new TestRecord("ghi", 3, 3.3, LocalDate.parse("2020-03-04"), LocalDateTime.parse("2020-03-04T05:06:07"));

    public static final TestRecord REC4 =
        new TestRecord("foo", 0, 4.4, null, null);

    public static final TestRecord REC5 =
        new TestRecord("bar", 0, Double.NaN, LocalDate.parse("2020-12-31"), null);

    public static TestRecord now(String key) {
        LocalDateTime time = LocalDateTime.now();

        int ival = 10000 * time.getYear() + 100 * time.getMonthValue() + time.getDayOfMonth();
        double dval = ival / 10000.00;

        return new TestRecord(key, ival, dval, time.toLocalDate(), time);
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof TestRecord) && equalsRecord((TestRecord) obj);
    }

    private boolean equalsRecord(TestRecord that) {
        return this.ival == that.ival
            && ObjectUtil.equals(this.key, that.key)
            && ObjectUtil.equals(this.date, that.date)
            && ObjectUtil.equals(this.time, that.time)
            && DoubleComparator.DEFAULT.EQ(this.dval, that.dval);
    }

    @Override public String toString() {
        return String.format("TestRecord(key = '%s', ival = %d, dval = %.4f, date = '%s', time = '%s')",
                             key, ival, dval, date, time);
    }
}
