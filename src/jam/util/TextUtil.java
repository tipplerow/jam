
package jam.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides utility classes for operating on free text.
 */
public final class TextUtil {
    /**
     * Splits text into words.
     *
     * @param text the text to split.
     *
     * @return the words contained in the text string.
     */
    public static List<String> splitWords(String text) {
        String[] wordArray = RegexUtil.MULTI_WHITE_SPACE.split(text);
        List<String> wordList = new ArrayList<String>(wordArray.length);

        for (String word : wordArray)
            wordList.add(trimWord(word));

        return wordList;
    }

    /**
     * Removes leading and trailing white space and punctuation from
     * a word.
     *
     * @param word the word to trim.
     *
     * @return the input word with leading and trailing white space
     * and punctuation removed.
     */
    public static String trimWord(String word) {
        int start = 0;

        while (start < word.length() && !isWordCharacter(word.charAt(start)))
            ++start;

        int end = word.length();

        while (end > start && !isWordCharacter(word.charAt(end - 1)))
            --end;

        if (start == 0 && end == word.length())
            return word;
        else
            return word.substring(start, end);
    }

    private static boolean isWordCharacter(char c) {
        return Character.isLetterOrDigit(c);
    }
}
