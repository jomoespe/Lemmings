package com.josemorenoesteban.lemmings.climber.client.v1;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Function;

public interface Climber {
    static final String SPEC_VERSION = "1.0.0-SNAPSHOT";
    
    static Climber of() {
        Iterator<Climber> servicesIterator = ServiceLoader.load(Climber.class).iterator();
        if (servicesIterator.hasNext()) {
            return servicesIterator.next();
        } else {
            throw new RuntimeException(String.format("%s implementation not found", Climber.class.getName()));
        }
    }

    static Climber of(final Function<String, List<String>> fallback) {
        return Climber.of().fallback( fallback );
    }
    
    static String getSpec() { return SPEC_VERSION; }
    String  getVersion();
    Climber fallback(final Function<String, List<String>> fallback);

    Climber question(final String question);
    Climber start(final int position);
    Climber size(final int size);
    List<String> fetch();
}
