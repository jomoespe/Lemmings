package com.josemorenoesteban.lemmings.basher.client.v1;

import static java.lang.String.format;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Basher<T> {
    static <T> Basher<T> of() {
        Iterator<Basher> servicesIterator = ServiceLoader.load(Basher.class).iterator();
        if (servicesIterator.hasNext()) {
            return servicesIterator.next();
        } else {
            throw new RuntimeException(format("%s implementation not found", Basher.class.getName()));
        }
    }

    static <T> Basher<T> withFallback(final Supplier<T> fallback) {
        return Basher.<T>of().fallback( fallback );
    }
    
    static String getSpec() { return "1.0.0-SNAPSHOT"; }
    String  getVersion();
    Basher fallback(final Supplier<T> fallback);

    Basher onMessage(final Consumer<T> incomingMessage);
    Basher onOpen(final Consumer<Map<String, Object>> userProperties);
    Basher onError(final Consumer<Throwable> throwable);
    Basher onClose(final Consumer<Integer> closingCode);
    void fetch();
}
