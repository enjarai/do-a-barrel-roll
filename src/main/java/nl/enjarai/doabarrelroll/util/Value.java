package nl.enjarai.doabarrelroll.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Value<T> extends Consumer<T>, Supplier<T> {
    static <T> Value<T> of(T value) {
        return new Value<>() {
            @Override
            public void accept(T t) {
            }

            @Override
            public T get() {
                return value;
            }
        };
    }
}
