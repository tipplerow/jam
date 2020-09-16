
package jam.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

final class FactorialList extends RecursiveList<Integer> {
    public FactorialList() {
        super();
    }

    public FactorialList(int initialSize) {
        super();
        get(initialSize - 1);
    }

    @Override protected Integer compute(int index) {
        if (index <= 1)
            return 1;
        else
            return index * get(index - 1);
    }
}

final class FibonacciList extends RecursiveList<Integer> {
    public FibonacciList() {
        super();
    }

    public FibonacciList(int initialSize) {
        super();
        get(initialSize - 1);
    }

    @Override protected Integer compute(int index) {
        if (index <= 1)
            return 1;
        else
            return get(index - 2) + get(index - 1);
    }
}

public class RecursiveListTest {
    private static final List<Integer> FACTORIAL_LIST = 
        Arrays.asList(1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800);

    private static final List<Integer> FIBONACCI_LIST = 
        Arrays.asList(1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144);

    @Test public void testFactorial() {
        FactorialList list = new FactorialList();
        assertTrue(list.isEmpty());

        assertEquals(FACTORIAL_LIST.get(5), list.get(5));
        assertEquals(6, list.size());

        assertEquals(FACTORIAL_LIST.get(10), list.get(10));
        assertEquals(FACTORIAL_LIST, list);
    }

    @Test public void testFibonacci() {
        FibonacciList list = new FibonacciList();
        assertTrue(list.isEmpty());

        assertEquals(FIBONACCI_LIST.get(5), list.get(5));
        assertEquals(6, list.size());

        assertEquals(FIBONACCI_LIST.get(11), list.get(11));
        assertEquals(FIBONACCI_LIST, list);
    }

    @Test public void testIterator() {
        FibonacciList list = new FibonacciList(3);
        Iterator<Integer> iter = list.iterator();

        assertTrue(iter.hasNext());
        assertEquals(Integer.valueOf(1), iter.next());

        assertTrue(iter.hasNext());
        assertEquals(Integer.valueOf(1), iter.next());

        assertTrue(iter.hasNext());
        assertEquals(Integer.valueOf(2), iter.next());

        assertFalse(iter.hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAdd() {
        FibonacciList list = new FibonacciList(10);
        list.add(5, 11111);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorRemove() {
        FibonacciList list = new FibonacciList(10);
        Iterator<Integer> iter = list.iterator();

        iter.next();
        iter.remove();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        FibonacciList list = new FibonacciList(10);
        list.remove(3);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.util.RecursiveListTest");
    }
}
