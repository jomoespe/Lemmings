package com.josemorenoesteban.lemmings.climber.service.port.rest;

import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.PathParameter;

import spark.Request;
import spark.Response;

class Documentation {
    
    Object swagger(final Request req, final Response res) {
        return new Swagger()
            .info(info())
            .path("get", getAsJSon());
    }

    Info info() {
        return new Info()
            .contact(new Contact().name("Jose Moreno").email("Jose.MorenoEsteban@zooplus.com"))
            .description("Test servide description")
            .license(new License().name("Apache?"))
            .title("Service title")
            .termsOfService("Terms of service")
            .version("1.0.0-SNAPSHOT");
    }
    
    public Path getAsJSon() {
        return new Path().get(new Operation()
                .description("Get the data")
                .parameter(new PathParameter().name("q").type("string").description("Query term"))
                .parameter(new PathParameter().name("start").type("integer").description("First element position"))
                .parameter(new PathParameter().name("size").type("integer").description("Number of elements to return"))
        );
    }
}
