
package jam.lang;

import org.junit.*;
import static org.junit.Assert.*;

class OrdinalClass extends Ordinal {
    public static final OrdinalIndex index = OrdinalIndex.create();

    public OrdinalClass() {
        super(index);
    }
}

public class OrdinalTest {
    @Test public void testIndex() {
        OrdinalClass obj0 = new OrdinalClass();
        OrdinalClass obj1 = new OrdinalClass();
        OrdinalClass obj2 = new OrdinalClass();

        assertEquals(0, obj0.getIndex());
        assertEquals(1, obj1.getIndex());
        assertEquals(2, obj2.getIndex());
        assertEquals(3, OrdinalClass.index.peek());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lang.OrdinalTest");
    }
}
