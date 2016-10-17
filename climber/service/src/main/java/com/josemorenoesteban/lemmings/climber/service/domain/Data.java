package com.josemorenoesteban.lemmings.climber.service.domain;

import static java.util.Optional.ofNullable;

import java.util.Optional;

public class Data {
    private final Integer id;
    private final String  name;
    
    public Data(final Integer id, final String name) {
        this.id   = id;
        this.name = name;
    }
    
    public Optional<Integer> id()   { return ofNullable(id); }
    public Optional<String>  name() { return ofNullable(name); }
}
