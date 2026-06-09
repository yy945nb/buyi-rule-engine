package com.ymware.engine.domain.rule.model;

/**
 * Exception thrown when no action provider supports the requested action type.
 */
public class UnsupportedActionException extends ActionCreationException {

    public UnsupportedActionException(String message) {
        super(message);
    }

    public UnsupportedActionException(String message, Throwable cause) {
        super(message, cause);
    }
}

