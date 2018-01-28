
package jam.vector;

import java.util.List;
import java.util.RandomAccess;

final class ListWrapper extends AbstractVector {
    private final List<Double> list;

    ListWrapper(List<Double> list) {
        validate(list);
        this.list = list;
    }

    private static void validate(List<Double> list) {
        if (!(list instanceof RandomAccess))
            throw new IllegalArgumentException("Only RandomAccess lists are supported.");
    }

    @Override public int length() {
        return list.size();
    }

    @Override public double getDouble(int index) {
        return list.get(index);
    }
}
