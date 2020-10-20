
package jam.collect;

import java.time.LocalDate;

import com.google.common.collect.TreeMultimap;

public final class CompKeyJoinIndex extends MultimapJoinIndex<String, LocalDate, CompKey> {
    private CompKeyJoinIndex() {
        super(TreeMultimap.create(), TreeMultimap.create());
    }

    public static CompKeyJoinIndex create() {
        return new CompKeyJoinIndex();
    }
}
