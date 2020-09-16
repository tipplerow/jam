
package jam.lang;

import org.junit.*;
import static org.junit.Assert.*;

public class LockableTest {
    @Test public void testUnset() {
        Lockable<Integer> lockable = Lockable.create();

        assertTrue(lockable.isUnlocked());
        assertTrue(lockable.isUnset());
        assertNull(lockable.get());

        lockable.set(3);
        
        assertTrue(lockable.isUnlocked());
        assertTrue(lockable.isSet());
        assertEquals(3, lockable.get().intValue());

        lockable.unset();
        lockable.lock();
        
        assertTrue(lockable.isLocked());
        assertTrue(lockable.isUnset());
        assertNull(lockable.get());
    }

    @Test public void testSet() {
        Lockable<Integer> lockable = Lockable.create(3);

        assertTrue(lockable.isUnlocked());
        assertTrue(lockable.isSet());
        assertEquals(3, lockable.get().intValue());

        lockable.set(-2);
        lockable.lock();
        
        assertTrue(lockable.isLocked());
        assertTrue(lockable.isSet());
        assertEquals(-2, lockable.get().intValue());
    }

    @Test(expected = IllegalStateException.class)
    public void testSetLocked() {
        Lockable<Integer> lockable = Lockable.create(3);
        lockable.lock();
        lockable.set(-2);
    }

    @Test(expected = IllegalStateException.class)
    public void testUnsetLocked() {
        Lockable<Integer> lockable = Lockable.create(3);
        lockable.lock();
        lockable.unset();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lang.LockableTest");
    }
}
