package com.ymware.engine.domain.rule.model;

/**
 * Exception thrown when a required resource is not found in the execution context.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = extractResourceName(message);
    }

    public ResourceNotFoundException(String resourceName, String message) {
        super(message);
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }

    private static String extractResourceName(String message) {
        if (message != null && message.contains(": ")) {
            String[] parts = message.split(": ", 2);
            if (parts.length == 2) {
                return parts[1];
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ResourceNotFoundException{" +
                "resourceName='" + resourceName + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}

