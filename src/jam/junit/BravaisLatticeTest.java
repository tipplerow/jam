
package jam.junit;

import java.util.List;

import jam.bravais.Lattice;
import jam.bravais.LatticeType;
import jam.bravais.Period;
import jam.bravais.UnitIndex;

import org.junit.*;
import static org.junit.Assert.*;

public class BravaisLatticeTest {
    @Test public void testSquare() {
        Lattice lattice = LatticeType.parse("SQUARE; 3, 4");

        assertEquals(2, lattice.period().dimensionality());
        assertEquals(3, lattice.period().period(0));
        assertEquals(4, lattice.period().period(1));

        assertEquals(2, lattice.unitCell().dimensionality());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.BravaisLatticeTest");
    }
}
