package com.josemorenoesteban.lemmings.climber.service.domain;

public class Data {
    private final Integer id;
    private final String  name;
    
    public Data(final Integer id, final String name) {
        this.id   = id;
        this.name = name;
    }
    
    public Integer id()   { return id; }
    public String  name() { return name; }
}
