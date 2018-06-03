
package jam.util;

import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;

/**
 * Provides utility methods for operating on {@code LongList} objects
 * from the {@code fastutil} package.
 */
public final class LongListUtil {
    /**
     * Formats a {@code LongList} as a delimited string.
     *
     * @param list the list to format.
     *
     * @param delim the field delimiter.
     *
     * @return a delimited string representation of the input list.
     */
    public static String format(LongList list, String delim) {
        StringBuilder builder = new StringBuilder();

        if (list.size() > 0)
            builder.append(list.getLong(0));

        for (int index = 1; index < list.size(); ++index) {
            builder.append(delim);
            builder.append(list.getLong(index));
        }

        return builder.toString();
    }

    /**
     * Parses a delimited string to extract a {@code LongList}.
     *
     * @param str the string to parse.
     *
     * @param delim the field delimiter pattern.
     *
     * @return a {@code LongList} containing the long values encoded
     * in the input string.
     */
    public static LongList parse(String str, Pattern delim) {
        str = str.trim();

        if (str.isEmpty())
            return LongLists.EMPTY_LIST;
            
        String[] fields = delim.split(str);
        LongList list   = new LongArrayList(fields.length);

        for (String field : fields)
            list.add(Long.parseLong(field.trim()));

        return list;
    }
}
