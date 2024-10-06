package nl.enjarai.doabarrelroll.api.event;

import java.util.List;
import java.util.function.Supplier;

public interface Event<T> {
    void register(T listener);

    void register(T listener, int priority);

    void register(T listener, Supplier<Boolean> enabled);

    void register(T listener, int priority, Supplier<Boolean> enabled);

    void unregister(T listener);

    List<T> getListeners();
}
