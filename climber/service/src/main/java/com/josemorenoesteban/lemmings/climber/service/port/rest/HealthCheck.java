package com.josemorenoesteban.lemmings.climber.service.port.rest;

import spark.Request;
import spark.Response;

import javax.json.Json;

class HealthCheck {
    Object check(final Request req, final Response res) {
        return  Json.createObjectBuilder().add("message", "It's alive. ALIVE!").build();
    }
}
