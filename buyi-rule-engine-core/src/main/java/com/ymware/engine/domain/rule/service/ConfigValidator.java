package com.ymware.engine.domain.rule.service;


import com.ymware.engine.domain.rule.service.FlowConfig;

/**
 * Base interface for all configuration validators.
 */
public interface ConfigValidator {


    /**
     * Validate the rule engine configuration.
     *
     * @param config The configuration to validate
     * @return ValidationResult containing any issues found
     */
    ValidationResult validate(FlowConfig config);

    /**
     * Get the name of this validator (for logging).
     */
    default String getValidatorName() {
        return this.getClass().getSimpleName();
    }
}


