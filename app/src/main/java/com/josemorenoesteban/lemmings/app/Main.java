package com.josemorenoesteban.lemmings.app;

import com.josemorenoesteban.lemmings.climber.client.v1.Climber;

public class Main {
    public static void main(String...args) {
        Climber climber = Climber.of();
        System.out.printf("Service spec: %s, version: %s\n", Climber.getSpec(), climber.getVersion());
        climber
            .question("t")
            .start(2)
            .size(2)
            .fetch()
            .forEach( System.out::println );
    }
}
