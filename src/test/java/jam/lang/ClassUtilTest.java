
package jam.lang;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    @Test public void testImplements() {
        Set<String> set = Set.of("abc", "def", "ghi");
        List<String> list = List.of("abc", "def", "ghi");

        assertTrue(ClassUtil.implements_(set, Collection.class));
        assertTrue(ClassUtil.implements_(set, Set.class));
        assertFalse(ClassUtil.implements_(set, List.class));

        assertTrue(ClassUtil.implements_(list, Collection.class));
        assertTrue(ClassUtil.implements_(list, List.class));
        assertFalse(ClassUtil.implements_(list, Set.class));

        assertFalse(ClassUtil.implements_(Integer.valueOf(1), Integer.class));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lang.ClassUtilTest");
    }
}
