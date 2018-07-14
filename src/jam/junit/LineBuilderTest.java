
package jam.junit;

import jam.report.LineBuilder;

import org.junit.*;
import static org.junit.Assert.*;

public class LineBuilderTest {
    @Test public void testBuilder() {
        LineBuilder builder = LineBuilder.csv();
        assertEquals("", builder.toString());

        builder.append(123);
        assertEquals("123", builder.toString());

        builder.append(345L);
        assertEquals("123,345", builder.toString());

        builder.append(1.23);
        assertEquals("123,345,1.23", builder.toString());

        builder.append(3.45E-8);
        assertEquals("123,345,1.23,3.45E-8", builder.toString());

        builder.append(3.45E-8, "%14.10f");
        assertEquals("123,345,1.23,3.45E-8,  0.0000000345", builder.toString());

        builder.append("FOO");
        assertEquals("123,345,1.23,3.45E-8,  0.0000000345,FOO", builder.toString());

        builder.append(Integer.valueOf(-5));
        assertEquals("123,345,1.23,3.45E-8,  0.0000000345,FOO,-5", builder.toString());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.LineBuilderTest");
    }
}
