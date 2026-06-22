package com.ymware.engine.domain.workflow.model;

import com.ymware.engine.domain.value.model.Parameter;
import com.ymware.engine.domain.workflow.type.RefType;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ParameterResolverTest {

    @Test
    void resolveWithNullParametersReturnsExecute() {
        Map<String, Object> execute = Map.of("key", "value");
        Map<String, Object> result = ParameterResolver.resolve(null, execute);
        assertEquals("value", result.get("key"));
    }

    @Test
    void resolveWithNullExecuteReturnsEmptyMap() {
        Map<String, Object> result = ParameterResolver.resolve(null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void resolveConstantParameterReturnsDefaultValue() {
        Parameter param = new Parameter();
        param.setName("greeting");
        param.setRefType(RefType.CONSTANT);
        param.setDefaultValue("hello");

        Map<String, Object> result = ParameterResolver.resolve(List.of(param), Map.of());
        assertEquals("hello", result.get("greeting"));
    }

    @Test
    void resolveRefParameterExtractsFromExecute() {
        Parameter param = new Parameter();
        param.setName("userName");
        param.setRefType(RefType.REF);
        param.setRefValue(List.of("userNode", "name"));

        Map<String, Object> execute = Map.of("userNode", Map.of("name", "Alice", "age", 30));
        Map<String, Object> result = ParameterResolver.resolve(List.of(param), execute);

        assertEquals("Alice", result.get("userName"));
    }

    @Test
    void resolveRefParameterWithSingleRefValue() {
        Parameter param = new Parameter();
        param.setName("directValue");
        param.setRefType(RefType.REF);
        param.setRefValue(List.of("someKey"));
        param.setDefaultValue("fallback");

        Map<String, Object> execute = Map.of("someKey", "direct");
        Map<String, Object> result = ParameterResolver.resolve(List.of(param), execute);

        assertEquals("direct", result.get("directValue"));
    }

    @Test
    void resolveRefParameterFallsBackToDefault() {
        Parameter param = new Parameter();
        param.setName("missing");
        param.setRefType(RefType.REF);
        param.setRefValue(List.of("nonexistent", "field"));
        param.setDefaultValue("fallback");

        Map<String, Object> result = ParameterResolver.resolve(List.of(param), Map.of());
        assertEquals("fallback", result.get("missing"));
    }

    @Test
    void resolveThrowsOnMissingRequiredParameter() {
        Parameter param = new Parameter();
        param.setName("required_field");
        param.setRefType(RefType.CONSTANT);
        param.setRequire(true);
        param.setDefaultValue(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ParameterResolver.resolve(List.of(param), Map.of()));
        assertTrue(ex.getMessage().contains("required_field"));
    }

    @Test
    void resolveFromMemoryExtractsRefValues() {
        Parameter param = new Parameter();
        param.setName("result");
        param.setRefType(RefType.REF);
        param.setRefValue(List.of("nodeA", "output"));

        Map<String, Object> memory = Map.of("nodeA", Map.of("output", 42));
        Map<String, Object> result = ParameterResolver.resolveFromMemory(List.of(param), memory);

        assertEquals(42, result.get("result"));
    }

    @Test
    void resolveFromMemoryWithEmptyParameters() {
        Map<String, Object> result = ParameterResolver.resolveFromMemory(Collections.emptyList(), Map.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void resolveFromMemoryWithNullParameters() {
        Map<String, Object> result = ParameterResolver.resolveFromMemory(null, Map.of());
        assertTrue(result.isEmpty());
    }
}
