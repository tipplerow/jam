
package jam.vector;

import java.util.List;
import java.util.RandomAccess;
import java.util.function.ToDoubleFunction;

final class ListMapper<E> extends AbstractVector {
    private final List<E> list;
    private final ToDoubleFunction<? super E> func;

    ListMapper(List<E> list, ToDoubleFunction<? super E> func) {
        validate(list);
        this.list = list;
        this.func = func;
    }

    private static void validate(List<?> list) {
        if (!(list instanceof RandomAccess))
            throw new IllegalArgumentException("Only RandomAccess lists are supported.");
    }

    @Override public int length() {
        return list.size();
    }

    @Override public double getDouble(int index) {
        return func.applyAsDouble(list.get(index));
    }
}
