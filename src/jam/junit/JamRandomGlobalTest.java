
package jam.junit;

import jam.math.JamRandom;

import org.junit.*;
import static org.junit.Assert.*;

public class JamRandomGlobalTest {

    @Test(expected = RuntimeException.class) 
    public void testGlobalUninitialized() {
        JamRandom.global();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.JamRandomGlobalTest");
    }
}
