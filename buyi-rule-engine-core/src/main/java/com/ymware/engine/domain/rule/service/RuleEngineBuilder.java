package com.ymware.engine.domain.rule.service;

import com.ymware.engine.domain.rule.action.ActionProvider;
import com.ymware.engine.domain.rule.action.ActionRegistry;
import com.ymware.engine.domain.rule.action.builtin.ScriptActionProvider;
import com.ymware.engine.domain.rule.model.ConfigurationLoadException;
import com.ymware.engine.config.ConfigurationLoader;
import com.ymware.engine.expression.ExpressionEvaluator;
import com.ymware.engine.expression.JexlExpressionEvaluator;
import com.ymware.engine.domain.rule.model.RuleExecutionException;import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating a RuleExecutor with all dependencies.
 */
public class RuleEngineBuilder {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineBuilder.class);

    private FlowConfig config;
    private ActionRegistry actionRegistry;
    private ExpressionEvaluator expressionEvaluator;
    private boolean validateConfig = true;
    private boolean includeBuiltInActions = true;
    private final List<ActionProvider> customProviders = new ArrayList<>();

    private RuleEngineBuilder() {
    }

    /**
     * Create a new builder.
     */
    public static RuleEngineBuilder create() {
        return new RuleEngineBuilder();
    }

    /**
     * Set the rule engine configuration.
     */
    public RuleEngineBuilder withConfig(FlowConfig config) {
        this.config = config;
        return this;
    }

    /**
     * Load configuration from a file.
     */
    public RuleEngineBuilder withConfigFile(String filePath) throws ConfigurationLoadException {
        ConfigurationLoader loader = new ConfigurationLoader();
        this.config = loader.loadFromFile(filePath);
        return this;
    }

    /**
     * Load configuration from classpath.
     */
    public RuleEngineBuilder withConfigFromClasspath(String resourcePath) throws ConfigurationLoadException {
        ConfigurationLoader loader = new ConfigurationLoader();
        this.config = loader.loadFromClasspath(resourcePath);
        return this;
    }

    /**
     * Set custom action registry.
     */
    public RuleEngineBuilder withActionRegistry(ActionRegistry actionRegistry) {
        this.actionRegistry = actionRegistry;
        return this;
    }

    /**
     * Set custom expression evaluator.
     */
    public RuleEngineBuilder withExpressionEvaluator(ExpressionEvaluator expressionEvaluator) {
        this.expressionEvaluator = expressionEvaluator;
        return this;
    }

    /**
     * Register a custom action provider.
     */
    public RuleEngineBuilder registerActionProvider(ActionProvider provider) {
        this.customProviders.add(provider);
        return this;
    }

    /**
     * Enable or disable configuration validation.
     */
    public RuleEngineBuilder withValidation(boolean validate) {
        this.validateConfig = validate;
        return this;
    }

    /**
     * Include or exclude built-in actions (ScriptAction).
     */
    public RuleEngineBuilder withBuiltInActions(boolean include) {
        this.includeBuiltInActions = include;
        return this;
    }

    /**
     * Build the RuleExecutor.
     */
    public RuleExecutor build() throws RuleExecutionException {
        // Validate required components
        if (config == null) {
            throw new RuleExecutionException("Configuration is required");
        }
        logger.info("Building RuleExecutor...");
        // Validate configuration if enabled
        if (validateConfig) {
            validateConfiguration();
        }
        // Create expression evaluator if not provided
        if (expressionEvaluator == null) {
            logger.debug("Creating default JEXL expression evaluator");
            expressionEvaluator = new JexlExpressionEvaluator();
        }
        // Create action registry if not provided
        if (actionRegistry == null) {
            logger.debug("Creating action registry");
            actionRegistry = new ActionRegistry();
            // Register built-in actions
            if (includeBuiltInActions) {
                logger.debug("Registering built-in ScriptAction");
                actionRegistry.registerProvider(new ScriptActionProvider(expressionEvaluator));
            }
            // Register custom providers
            if (!customProviders.isEmpty()) {
                logger.debug("Registering {} custom action providers", customProviders.size());
                actionRegistry.registerProviders(customProviders);
            }
        }

        // Create and return executor
        RuleExecutor executor = new RuleExecutor(config, actionRegistry, expressionEvaluator);
        logger.info("RuleExecutor built successfully with {} rules",
                config.getRuleCount());
        return executor;
    }

    /**
     * Validate the configuration.
     */
    private void validateConfiguration() throws RuleExecutionException {
        logger.debug("Validating configuration...");
        CompositeValidator validator = CompositeValidator.createDefault(true);
        ValidationResult result = validator.validate(config);
        if (result.hasErrors()) {
            String errorMsg = "Configuration validation failed:\n" + result.getSummary();
            logger.error(errorMsg);
            throw new RuleExecutionException(errorMsg);
        }
        if (result.hasWarnings()) {
            logger.warn("Configuration has warnings:\n{}", result.getSummary());
        }

        logger.debug("Configuration validation passed");
    }
}

