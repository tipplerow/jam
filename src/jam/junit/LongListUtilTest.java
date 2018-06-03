
package jam.junit;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import jam.util.LongListUtil;
import jam.util.RegexUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class LongListUtilTest {
    @Test public void testFormat() {
        LongList list = new LongArrayList();
        assertEquals("", LongListUtil.format(list, ","));

        list.add(3);
        assertEquals("3", LongListUtil.format(list, ","));

        list.add(6);
        assertEquals("3,6", LongListUtil.format(list, ","));

        list.add(9);
        assertEquals("3,6,9", LongListUtil.format(list, ","));
    }

    @Test public void testParse() {
        LongList list = new LongArrayList();
        assertEquals(list, LongListUtil.parse("  ", RegexUtil.COMMA));

        list.add(3);
        assertEquals(list, LongListUtil.parse(" 3 ", RegexUtil.COMMA));

        list.add(6);
        assertEquals(list, LongListUtil.parse(" 3 , 6 ", RegexUtil.COMMA));

        list.add(9);
        assertEquals(list, LongListUtil.parse(" 3 , 6 , 9 ", RegexUtil.COMMA));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.LongListUtilTest");
    }
}
