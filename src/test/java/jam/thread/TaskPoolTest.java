
package jam.thread;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jam.app.JamLogger;
import jam.math.JamRandom;

import org.junit.*;
import static org.junit.Assert.*;

public class TaskPoolTest {
    private final Set<String> keys = new TreeSet<String>();

    private static final String key1 = "abc";
    private static final String key2 = "def";
    private static final String key3 = "ghi";

    private final class Task implements Runnable {
        private final String key;

        private Task(String key) {
            this.key = key;
        }

        @Override public void run() {
            JamLogger.info("[%s]: Running thread...", key);

            try {
                int millis = JamRandom.global().nextInt(1000);
                JamLogger.info("[%s]: Sleeping [%d]...", key, millis);
                Thread.sleep(millis);
            }
            catch (InterruptedException ex) {
            }

            synchronized (keys) {
                keys.add(key);
            }

            JamLogger.info("[%s]: DONE!", key);
        }
    }

    @Test public void testRun() {
        keys.clear();
        TaskPool.run(List.of(new Task(key1), new Task(key2), new Task(key3)));
        assertEquals(Set.of(key1, key2, key3), keys);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.thread.TaskPoolTest");
    }
}
