package com.josemorenoesteban.lemmings.climber.service;

import static spark.Spark.stop;
import static java.util.Arrays.asList;

import com.josemorenoesteban.lemmings.climber.service.domain.Data;
import com.josemorenoesteban.lemmings.climber.service.port.rest.Query;
import com.josemorenoesteban.lemmings.climber.service.port.rest.RestPort;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Main {
    public static final String  DEFAULT_QUESTION = "";
    public static final Integer DEFAULT_START    = 0;
    public static final Integer DEFAULT_SIZE     = 10;
    
    public static void main(String...args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> { 
            System.out.println("Stopping al HTTP routes");
            stop(); 
            System.out.println("HTTP routes stoped");
        }, "my shutdown hook"));
        
        new RestPort( new Main().dataSupplier(DATA) );
    }
    
    private Function<Query, Stream<Data>> dataSupplier(final List<Data> dataList) {
        return (Query q) -> {
            return dataList
                .subList( q.start().orElse(DEFAULT_START), DATA.size() )
                .stream()
                .filter( data -> data.name().startsWith(q.question().orElse(DEFAULT_QUESTION)) )
                .limit( q.limit().orElse(DEFAULT_SIZE) );
        };
    }
    
    private static final List<Data>  DATA = asList(
        new Data(1,  "one"),
        new Data(2,  "two"),
        new Data(3,  "three"),
        new Data(4,  "four"),
        new Data(5,  "five"),
        new Data(6,  "six"),
        new Data(7,  "seven"),
        new Data(8,  "eight"),
        new Data(9,  "nine"),
        new Data(10, "ten"),
        new Data(11, "eleven"),
        new Data(12, "twelve")
    );
}
