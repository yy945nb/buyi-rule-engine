package com.ymware.engine.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.ymware.engine.domain.rule.model.ConfigurationLoadException;import com.fasterxml.jackson.databind.ObjectMapper;
import com.ymware.engine.domain.rule.service.FlowConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Loads rule engine configuration from various sources (file, classpath, string).
 */
public class ConfigurationLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);

    private final ObjectMapper objectMapper;

    public ConfigurationLoader() {
        this(createDefaultObjectMapper());
    }

    public ConfigurationLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Create a default ObjectMapper with recommended settings.
     */
    private static ObjectMapper createDefaultObjectMapper() {

        ObjectMapper mapper = new ObjectMapper();
        // Don't fail on unknown properties (forward compatibility)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Fail on null values for primitive types
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        // Accept single value as array
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return mapper;
    }

    /**
     * Load configuration from a file path.
     *
     * @param filePath Path to the JSON configuration file
     * @return Parsed FlowConfig
     * @throws ConfigurationLoadException if loading fails
     */
    public FlowConfig loadFromFile(String filePath) throws ConfigurationLoadException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new ConfigurationLoadException("File path cannot be null or empty");
        }
        logger.info("Loading rule engine configuration from file: {}", filePath);
        try {
            Path path = Paths.get(filePath);

            if (!Files.exists(path)) {
                throw new ConfigurationLoadException("Configuration file not found: " + filePath);
            }

            if (!Files.isReadable(path)) {
                throw new ConfigurationLoadException("Configuration file is not readable: " + filePath);
            }

            String content = Files.readAllLines(path, StandardCharsets.UTF_8).toString();
            return loadFromString(content, "file:" + filePath);

        } catch (IOException e) {
            throw new ConfigurationLoadException("Failed to read configuration file: " + filePath, e);
        }
    }

    /**
     * Load configuration from a File object.
     */
    public FlowConfig loadFromFile(File file) throws ConfigurationLoadException {
        if (file == null) {
            throw new ConfigurationLoadException("File cannot be null");
        }

        return loadFromFile(file.getAbsolutePath());
    }

    /**
     * Load configuration from classpath resource.
     *
     * @param resourcePath Classpath resource path (e.g., "rules/config.json")
     * @return Parsed FlowConfig
     * @throws ConfigurationLoadException if loading fails
     */
    public FlowConfig loadFromClasspath(String resourcePath) throws ConfigurationLoadException {
        if (resourcePath == null || resourcePath.trim().isEmpty()) {
            throw new ConfigurationLoadException("Resource path cannot be null or empty");
        }

        logger.info("Loading rule engine configuration from classpath: {}", resourcePath);

        // Remove leading slash if present
        String cleanPath = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(cleanPath)) {
            if (inputStream == null) {
                throw new ConfigurationLoadException("Classpath resource not found: " + resourcePath);
            }

            return loadFromInputStream(inputStream, "classpath:" + resourcePath);

        } catch (IOException e) {
            throw new ConfigurationLoadException("Failed to read classpath resource: " + resourcePath, e);
        }
    }

    /**
     * Load configuration from an InputStream.
     *
     * @param inputStream The input stream containing JSON configuration
     * @param source Description of the source (for error messages)
     * @return Parsed FlowConfig
     * @throws ConfigurationLoadException if loading fails
     */
    public FlowConfig loadFromInputStream(InputStream inputStream, String source)
            throws ConfigurationLoadException {
        if (inputStream == null) {
            throw new ConfigurationLoadException("InputStream cannot be null");
        }
        logger.debug("Loading rule engine configuration from InputStream: {}", source);
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String content = result.toString(StandardCharsets.UTF_8.name());
            return loadFromString(content, source);

        } catch (IOException e) {
            throw new ConfigurationLoadException("Failed to read from InputStream: " + source, e);
        }
    }

    /**
     * Load configuration from a JSON string.
     *
     * @param jsonContent The JSON configuration as a string
     * @return Parsed FlowConfig
     * @throws ConfigurationLoadException if parsing fails
     */
    public FlowConfig loadFromString(String jsonContent) throws ConfigurationLoadException {
        return loadFromString(jsonContent, "string");
    }

    /**
     * Load configuration from a JSON string with source information.
     *
     * @param jsonContent The JSON configuration as a string
     * @param source Description of the source (for error messages)
     * @return Parsed FlowConfig
     * @throws ConfigurationLoadException if parsing fails
     */
    public FlowConfig loadFromString(String jsonContent, String source)
            throws ConfigurationLoadException {

        if (jsonContent == null || jsonContent.trim().isEmpty()) {
            throw new ConfigurationLoadException("JSON content cannot be null or empty");
        }

        logger.debug("Parsing rule engine configuration from {}", source);

        try {
            FlowConfig config = objectMapper.readValue(jsonContent, FlowConfig.class);

            logger.info("Successfully loaded configuration from {}: version={}, entryPoint={}, rules={}",
                       source, config.getVersion(), config.getEntryPoint(), config.getRuleCount());

            return config;

        } catch (IOException e) {
            throw new ConfigurationLoadException(
                "Failed to parse JSON configuration from " + source + ": " + e.getMessage(), e);
        }
    }

    /**
     * Load configuration with wrapper handling.
     * Some configurations wrap the rules in a "ruleEngineConfig" object.
     * This method handles both formats.
     */
    public FlowConfig loadFromStringWithWrapper(String jsonContent)
            throws ConfigurationLoadException {

        if (jsonContent == null || jsonContent.trim().isEmpty()) {
            throw new ConfigurationLoadException("JSON content cannot be null or empty");
        }

        try {
            // Try to parse as-is first
            return loadFromString(jsonContent, "string");

        } catch (ConfigurationLoadException e) {
            // If that fails, try to extract from wrapper
            logger.debug("Direct parsing failed, attempting to parse with wrapper");

            try {
                ConfigWrapper wrapper = objectMapper.readValue(jsonContent, ConfigWrapper.class);
                if (wrapper.ruleEngineConfig != null) {
                    logger.info("Successfully extracted configuration from wrapper");
                    return wrapper.ruleEngineConfig;
                }

                // If wrapper exists but config is null, throw original error
                throw e;

            } catch (IOException wrapperException) {
                // Wrapper parsing also failed, throw original error
                throw e;
            }
        }
    }

    /**
     * Save configuration to a file.
     *
     * @param config The configuration to save
     * @param filePath Path where to save the configuration
     * @throws ConfigurationLoadException if saving fails
     */
    public void saveToFile(FlowConfig config, String filePath)
            throws ConfigurationLoadException {

        if (config == null) {
            throw new ConfigurationLoadException("Configuration cannot be null");
        }

        if (filePath == null || filePath.trim().isEmpty()) {
            throw new ConfigurationLoadException("File path cannot be null or empty");
        }

        logger.info("Saving rule engine configuration to file: {}", filePath);

        try {
            Path path = Paths.get(filePath);

            // Create parent directories if they don't exist
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            String json = objectMapper.writerWithDefaultPrettyPrinter()
                                     .writeValueAsString(config);

            Files.write(path, json.getBytes(StandardCharsets.UTF_8));
            logger.info("Successfully saved configuration to: {}", filePath);

        } catch (IOException e) {
            throw new ConfigurationLoadException("Failed to save configuration to file: " + filePath, e);
        }
    }

    /**
     * Convert configuration to JSON string.
     *
     * @param config The configuration to convert
     * @return JSON string representation
     * @throws ConfigurationLoadException if conversion fails
     */
    public String toJsonString(FlowConfig config) throws ConfigurationLoadException {
        return toJsonString(config, true);
    }

    /**
     * Convert configuration to JSON string with optional pretty printing.
     *
     * @param config The configuration to convert
     * @param prettyPrint Whether to format the JSON
     * @return JSON string representation
     * @throws ConfigurationLoadException if conversion fails
     */
    public String toJsonString(FlowConfig config, boolean prettyPrint)
            throws ConfigurationLoadException {

        if (config == null) {
            throw new ConfigurationLoadException("Configuration cannot be null");
        }

        try {
            if (prettyPrint) {
                return objectMapper.writerWithDefaultPrettyPrinter()
                                  .writeValueAsString(config);
            } else {
                return objectMapper.writeValueAsString(config);
            }
        } catch (IOException e) {
            throw new ConfigurationLoadException("Failed to convert configuration to JSON", e);
        }
    }

    /**
     * Get the ObjectMapper used by this loader.
     * Useful for customization.
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Wrapper class for handling JSON with "ruleEngineConfig" wrapper.
     */
    private static class ConfigWrapper {
        public FlowConfig ruleEngineConfig;
    }
}

