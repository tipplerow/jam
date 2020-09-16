
package jam.lang;

import org.junit.*;
import static org.junit.Assert.*;

class A {}
class B extends A {}
class C extends B {}

public class ClassUtilTest {
    @Test public void testIsA() {
        assertTrue(ClassUtil.isa(A.class, A.class));
        assertFalse(ClassUtil.isa(A.class, B.class));
        assertFalse(ClassUtil.isa(A.class, C.class));
        
        assertTrue(ClassUtil.isa(B.class, A.class));
        assertTrue(ClassUtil.isa(B.class, B.class));
        assertFalse(ClassUtil.isa(B.class, C.class));
        
        assertTrue(ClassUtil.isa(C.class, A.class));
        assertTrue(ClassUtil.isa(C.class, B.class));
        assertTrue(ClassUtil.isa(C.class, C.class));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lang.ClassUtilTest");
    }
}
