
package jam.junit;

import jam.lattice.Image;

import org.junit.*;
import static org.junit.Assert.*;

public class ImageTest {
    @Test public void testEquals() {
	Image image123A = Image.at(1, 2, 3);
	Image image123B = Image.at(1, 2, 3);
	Image image023  = Image.at(0, 2, 3);
	Image image103  = Image.at(1, 0, 3);
	Image image120  = Image.at(1, 2, 0);

	assertFalse(image123A.equals(null));

	assertTrue(image123A.equals(image123A));
	assertTrue(image123A.equals(image123B));

	assertFalse(image123A.equals(image023));
	assertFalse(image123A.equals(image103));
	assertFalse(image123A.equals(image120));
    }

    @Test public void testHashCode() {
	Image image123A = Image.at(1, 2, 3);
	Image image123B = Image.at(1, 2, 3);
	Image image023  = Image.at(0, 2, 3);
	Image image103  = Image.at(1, 0, 3);
	Image image120  = Image.at(1, 2, 0);

	assertTrue(image123A.hashCode() == image123A.hashCode());
	assertTrue(image123A.hashCode() == image123B.hashCode());

	assertTrue(image123A.hashCode() != image023.hashCode());
	assertTrue(image123A.hashCode() != image103.hashCode());
	assertTrue(image123A.hashCode() != image120.hashCode());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ImageTest");
    }
}
