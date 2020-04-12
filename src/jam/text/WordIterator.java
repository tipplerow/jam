
package jam.text;

import java.util.Iterator;

/**
 * Iterates sequentially over distinct words in a text sample.
 */
public final class WordIterator implements Iterator<String> {
    private final String text;

    // The index of the character that begins the next word...
    private int begin;

    private WordIterator(String text) {
        this.text = text;
        this.begin = TextUtil.nextWordIndex(text, 0);
    }

    /**
     * Returns a new iterator over a given text sample.
     *
     * <p>Note that the iterator is read-only: it does not
     * support the {@code remove} operation.
     *
     * @param text the text to process.
     *
     * @return an iterator for the specified sample text.
     */
    public static WordIterator split(String text) {
        return new WordIterator(text);
    }

    @Override public boolean hasNext() {
        return begin < text.length();
    }

    @Override public String next() {
        int nextBeg = begin;
        int nextEnd = findEnd();

        String nextString =
            text.substring(nextBeg, nextEnd);

        begin = TextUtil.nextWordIndex(text, nextEnd);
        return nextString;
    }

    private int findEnd() {
        int end = begin;

        while (end < text.length() && !TextUtil.wordEnds(text, end))
            ++end;

        // Add one to conform with the half-open (inclusive/exclusive)
        // (start, end] substring indexing...
        return end + 1;
    }

    @Override public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
