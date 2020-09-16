
package jam.text;

import java.util.Collection;
import java.util.List;

/**
 * Searches text for target words or phrases.
 *
 * <p>The matching algorithm implemented in this class splits sample
 * text into distinct words and then finds the first sample word or
 * phrase that exactly matches one of the target words or phrases
 * (with case sensitivity).  Matching occurs only at word boundaries,
 * so the target {@code stand}, for example, will match the sample
 * text {@code The witness took the stand.} but not {@code The witness
 * did not understand the question.} or {@code He bombed as a stand-up
 * comic.}
 */
public final class TextMatcher {
    private final boolean ignoreCase;
    private final Collection<String> targets;

    private TextMatcher(Collection<String> targets, boolean ignoreCase) {
        this.targets = targets;
        this.ignoreCase = ignoreCase;
    }

    /**
     * Creates a new text matcher for a fixed collection of target
     * words or phrases.
     *
     * @param ignoreCase whether to ignore case when matching strings.
     *
     * @param targets the words or phrases to be matched against.
     *
     * @return a new text filter for the specified targets.
     */
    public static TextMatcher create(boolean ignoreCase, String... targets) {
        return create(List.of(targets), ignoreCase);
    }

    /**
     * Creates a new text matcher for a fixed collection of target
     * words or phrases.
     *
     * @param targets the words or phrases to be matched against.
     *
     * @param ignoreCase whether to ignore case when matching strings.
     *
     * @return a new text filter for the specified targets.
     */
    public static TextMatcher create(Collection<String> targets, boolean ignoreCase) {
        return new TextMatcher(targets, ignoreCase);
    }

    /**
     * Creates a new case-sensitive text matcher for a fixed
     * collection of target words or phrases.
     *
     * @param targets the words or phrases to be matched against.
     *
     * @return a new case-sensitive text filter for the specified
     * targets.
     */
    public static TextMatcher caseSensitive(String... targets) {
        return caseSensitive(List.of(targets));
    }

    /**
     * Creates a new case-sensitive text matcher for a fixed
     * collection of target words or phrases.
     *
     * @param targets the words or phrases to be matched against.
     *
     * @return a new case-sensitive text filter for the specified
     * targets.
     */
    public static TextMatcher caseSensitive(Collection<String> targets) {
        return create(targets, false);
    }

    /**
     * Creates a new case-insensitive text matcher for a fixed
     * collection of target words or phrases.
     *
     * @param targets the words or phrases to be matched against.
     *
     * @return a new case-insensitive text filter for the specified
     * targets.
     */
    public static TextMatcher ignoreCase(String... targets) {
        return ignoreCase(List.of(targets));
    }

    /**
     * Creates a new case-insensitive text matcher for a fixed
     * collection of target words or phrases.
     *
     * @param targets the words or phrases to be matched against.
     *
     * @return a new case-insensitive text filter for the specified
     * targets.
     */
    public static TextMatcher ignoreCase(Collection<String> targets) {
        return create(targets, true);
    }

    /**
     * Determines whether a text sample contains one or more of the
     * target words or phrases.
     *
     * @param text the sample text to search.
     *
     * @return {@code true} iff the sample text contains one or more
     * of the target words or phrases.
     */
    public boolean matches(String text) {
        return matchOne(text) != null;
    }

    /**
     * Attempts to find at least one target word or phrase that occurs
     * in a text sample.
     *
     * @param text the sample text to search.
     *
     * @return a matching target word or phrase, if one can be found;
     * {@code null} otherwise.
     */
    public String matchOne(String text) {
        int begin = TextUtil.nextWordIndex(text, 0);

        while (begin < text.length()) {
            String target = matchOne(text, begin);

            if (target != null)
                return target;

            begin = TextUtil.nextWordIndex(text, begin + 1);
        }

        return null;
    }

    private String matchOne(String text, int begin) {
        for (String target : targets)
            if (isMatch(target, text, begin))
                return target;

        return null;
    }

    private boolean isMatch(String target, String text, int begin) {
        //
        // The sample text contains the target word or phrase at the
        // specified location if and only if (1) the sample substring
        // starting at that location is equal to the target, AND (2)
        // the matching substring ends at a word boundary.  We do not
        // allow for partial matches ending within the body of the
        // sample text.
        //
        // Matches are rare, so we want to identify non-matches as
        // efficiently as possible.  Let:
        //
        //     targetEnd = begin + target.length()
        //
        // be the ending index of the substring to be matched.  Note
        // that it is ONE GREATER THAN the sample index of the last
        // character, to be consistent with the indexing used by the
        // String.substring method.
        //
        // We first identify non-matches if targetEnd > text.length().
        // Next we identify non-matches if the character at targetEnd
        // is not a right word boundary character. If neither of these
        // conditions are met, we actually compare the substrings.
        //
        int targetEnd = begin + target.length();

        if (targetEnd > text.length())
            return false;

        if (targetEnd < text.length() && !TextUtil.isRightWordBoundary(text.charAt(targetEnd)))
            return false;

        return text.regionMatches(ignoreCase, begin, target, 0, target.length());
    }
}
