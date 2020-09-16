
package jam.util;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class ListViewTest {
    private static ListView<String> emptyView() {
        List<String> list = new ArrayList<String>();
        fillList(list);
        return ListView.create(list);
    }

    private static void fillList(List<String> list) {
        list.add("abc");
        list.add("def");
        list.add("ghi");
    }

    private void runTest(List<String> list) {
        ListView<String> view = ListView.create(list);

        assertEquals(3, view.size());
        assertEquals("abc", view.get(0));
        assertEquals("def", view.get(1));
        assertEquals("ghi", view.get(2));
    }

    @Test public void testArrayList() {
        List<String> list = new ArrayList<String>();
        fillList(list);
        runTest(list);
    }

    @Test public void testArraysToList() {
        List<String> list = Arrays.asList("abc", "def", "ghi");
        runTest(list);
    }

    @Test public void testLinkedList() {
        List<String> list = new LinkedList<String>();
        fillList(list);
        runTest(list);
    }

    @Test public void testVarArgs() {
        ListView<String> view = ListView.create("abc", "def", "ghi");

        assertEquals(3, view.size());
        assertEquals("abc", view.get(0));
        assertEquals("def", view.get(1));
        assertEquals("ghi", view.get(2));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAdd() {
        ListView<String> view = emptyView();
        view.add("foo");
    }


    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        ListView<String> view = emptyView();
        view.remove(1);
    }


    @Test(expected = UnsupportedOperationException.class)
    public void testSet() {
        ListView<String> view = emptyView();
        view.set(1, "foo");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.util.ListViewTest");
    }
}
