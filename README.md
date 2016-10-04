Lemmings, a microservices sample project 
========================================

[![Build Status](https://travis-ci.org/jomoespe/lemmings.svg?branch=master)](https://travis-ci.org/jomoespe/lemmings)

The lemmings, as the microservices, are things with a small responsability that do one thing but do it well, and work together quite well.

The lemmings types are: Climber, Floater, Bomber, Blocker, Builder, Basher, Miner and Digger (from http://lemmings.wikia.com/wiki/Lemmings)


Climber
-------

First microservice. Returns a list with number names

  /v1/climber[?[q=<search_term>][&start=<position_offset>][&size=<number_of_registries>]]


### Start the cimber microservice 

In `climber/service` directory...

    mvn exec:java -Dexec.mainClass="com.josemorenoesteban.lemmings.climber.service.Main"
    curl http://localhost:4567/v1/climber


The application
---------------

### Start the application

In `app` directory...

    mvn exec:java -Dexec.mainClass="com.josemorenoesteban.lemmings.app.Gui"
