
package jam.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jam.util.RegexUtil;

/**
 * Provides utility classes for operating on free text.
 */
public final class TextUtil {
    /**
     * The 100 most common words in English.
     */
    public static final Set<String> TOP_100 =
        Set.of("a", "about", "all", "also", "and",
               "as", "at", "be", "because", "but",
               "by", "can", "come", "could", "day",
               "do", "even", "find", "first", "for",
               "from", "get", "give", "go", "have",
               "he", "her", "here", "him", "his",
               "how", "I", "if", "in", "into",
               "it", "its", "just", "know", "like",
               "look", "make", "man", "many", "me",
               "more", "my", "new", "no", "not",
               "now", "of", "on", "one", "only",
               "or", "other", "our", "out", "people",
               "say", "see", "she", "so", "some",
               "take", "tell", "than", "that", "the",
               "their", "them", "then", "there", "these",
               "they", "thing", "think", "this", "those",
               "time", "to", "two", "up", "use",
               "very", "want", "way", "we", "well",
               "what", "when", "which", "who", "will",
               "with", "would", "year", "you", "your");

    /**
     * The closing bracket character.
     */
    public static final char CLOSE_BRACKET = ']';

    /**
     * The closing parenthesis character.
     */
    public static final char CLOSE_PAREN = ')';

    /**
     * The colon character.
     */
    public static final char COLON = ':';

    /**
     * The comma character.
     */
    public static final char COMMA = ',';

    /**
     * The double quote character.
     */
    public static final char DOUBLE_QUOTE = '"';

    /**
     * The exclamation mark character.
     */
    public static final char EXCLAMATION_MARK = '!';

    /**
     * The opening bracket character.
     */
    public static final char OPEN_BRACKET = '[';

    /**
     * The opening parenthesis character.
     */
    public static final char OPEN_PAREN = '(';

    /**
     * The period character.
     */
    public static final char PERIOD = '.';

    /**
     * The question mark character.
     */
    public static final char QUESTION_MARK = '?';

    /**
     * The semicolon character.
     */
    public static final char SEMI_COLON = ';';

    /**
     * The single quote character.
     */
    public static final char SINGLE_QUOTE = '\'';

    /**
     * Identifies characters that denote the beginning of a new word:
     * they occur immediately before the start of a new word.
     *
     * @param c the character to examine.
     *
     * @return {@code true} iff the specified character denotes a left
     * word boundary: a new word begins to the right of this character.
     */
    public static boolean isLeftWordBoundary(char c) {
        //
        // New words begin after white space, double quotation mark,
        // and opening bracket or parenthesis...
        //
        return Character.isWhitespace(c)
            || (c == DOUBLE_QUOTE)
            || (c == OPEN_BRACKET)
            || (c == OPEN_PAREN);
    }        

    /**
     * Identifies characters that denote the end of a word: they occur
     * immediately after the end of a word.
     *
     * @param c the character to examine.
     *
     * @return {@code true} iff the specified character denotes a
     * right word boundary: a word ends to the left of this character.
     */
    public static boolean isRightWordBoundary(char c) {
        //
        // Words end before white space, punctuation characters, 
        // and closing bracket or parenthesis...
        //
        return Character.isWhitespace(c)
            || isPhraseEndingPunctuation(c)
            || (c == CLOSE_BRACKET)
            || (c == CLOSE_PAREN);
    }

    /**
     * Defines punctuation characters that may indicate the end of a
     * phrase.
     *
     * @param c the character to examine.
     *
     * @return {@code true} iff the character may indicate the end of
     * a phrase.
     */
    public static boolean isPhraseEndingPunctuation(char c) {
        return isSentenceEndingPunctuation(c) 
            || (c == COMMA)
            || (c == COLON)
            || (c == SEMI_COLON)
            || (c == DOUBLE_QUOTE);
    }

    /**
     * Defines punctuation characters that may indicate the end of a
     * sentence.
     *
     * @param c the character to examine.
     *
     * @return {@code true} iff the character may indicate the end of
     * a sentence.
     */
    public static boolean isSentenceEndingPunctuation(char c) {
        return (c == PERIOD) || (c == QUESTION_MARK) || (c == EXCLAMATION_MARK);
    }

    /**
     * Finds the location of the next new word in a text sample.
     *
     * @param text the text to search.
     *
     * @param begin the index where the search will begin (characters
     * to the left of this index are ignored).
     *
     * @return the index of the character that starts the next word at
     * or after the {@code from} index; {@code text.length()} if there
     * are no more new words.
     *
     * @throws IllegalArgumentException if the {@code from} index is
     * negative.
     */
    public static int nextWordIndex(String text, int begin) {
        int index = begin;

        while (index < text.length() && !wordBegins(text, index))
            ++index;

        return index;
    }

    /**
     * Splits text into words.
     *
     * @param text the text to split.
     *
     * @return the words contained in the text string.
     */
    public static List<String> splitWords(String text) {
        List<String> wordList = new ArrayList<String>();
        WordIterator iterator = WordIterator.split(text);

        while (iterator.hasNext())
            wordList.add(iterator.next());

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

    /**
     * Determines whether a word begins at a particular location
     * in some text.
     *
     * @param text the text to examine.
     *
     * @param index the location of the potential word boundary.
     *
     * @return {@code true} iff a word begins at the specified
     * location.
     */
    public static boolean wordBegins(String text, int index) {
        //
        // For generic search terms, the first character may be a
        // letter or digit: "3M" is a valid "word"...
        //
        if (!Character.isLetterOrDigit(text.charAt(index)))
            return false;

        // The previous character must be a left word boundary...
        return (index == 0) || isLeftWordBoundary(text.charAt(index - 1));
    }

    /**
     * Determines whether a word ends at a particular location
     * in some text.
     *
     * @param text the text to examine.
     *
     * @param index the location of the potential word boundary.
     *
     * @return {@code true} iff a word ends at the specified
     * location.
     */
    public static boolean wordEnds(String text, int index) {
        //
        // For generic search terms, the last character may be a
        // letter or digit: "TLR1" is a valid "word"...
        //
        if (!Character.isLetterOrDigit(text.charAt(index)))
            return false;

        // The next character must be a word boundary...
        return (index == text.length() - 1) || isRightWordBoundary(text.charAt(index + 1));
    }
}
