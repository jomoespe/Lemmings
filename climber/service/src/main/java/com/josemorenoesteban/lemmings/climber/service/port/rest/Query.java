package com.josemorenoesteban.lemmings.climber.service.port.rest;

import spark.Request;

import java.util.Objects;
import java.util.Optional;

public final class Query {
    private static final String QUESTION_PARAM    = "q";
    private static final String START_PARAM       = "start";
    private static final String SIZE_PARAM        = "size";

    private final String  question;
    private final Integer start;
    private final Integer limit;

    public Query(final Request request) {
        question = request.queryParams(QUESTION_PARAM);
        start    = valueOf(request.queryParams(START_PARAM));
        limit    = valueOf(request.queryParams(SIZE_PARAM)); 
    }

    public Optional<String>  question() { return Optional.ofNullable(question); }
    public Optional<Integer> start()    { return Optional.ofNullable(start); }
    public Optional<Integer> limit()    { return Optional.ofNullable(limit); }

    private Integer valueOf(final String number) {
        try {
            String value = Objects.toString(number);
            return value != null ? Integer.parseInt(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
