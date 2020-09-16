
package jam.junit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Provides a base class for all JUnit tests in the {@code jam} project.
 */
public abstract class JamTestBase {
    public <V> void assertCollection(Collection<V> expected, Collection<V> actual) {
        Set<V> actualSet = new HashSet<V>(actual);
        Set<V> expectedSet = new HashSet<V>(expected);

        assertEquals(expectedSet, actualSet);
    }
}
