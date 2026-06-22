package com.ymware.engine.expression.unified;

import com.ymware.engine.compiler.CompilerEngine;
import com.ymware.engine.compiler.CompilerEngineType;
import com.ymware.engine.compiler.pojo.CompileResult;
import com.ymware.engine.compiler.pojo.CompileResultCode;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 动态编译引擎适配器 - 包装 Java/Groovy 动态编译
 * 将代码编译为类并执行
 */
public class CompilerEngineAdapter implements ExpressionEngineAdapter {

    private final CompilerEngine compilerEngine;
    private final ExpressionEngineType engineType;

    public CompilerEngineAdapter(CompilerEngineType compilerEngine) {
        this.compilerEngine = compilerEngine;
        this.engineType = compilerEngine == CompilerEngineType.JAVA ? ExpressionEngineType.JAVA : ExpressionEngineType.GROOVY;
    }

    @Override
    public ExpressionEngineType getEngineType() {
        return engineType;
    }

    @Override
    public ExpressionResult evaluate(String expression, ExpressionContext context) {
        long start = System.currentTimeMillis();
        try {
            CompileResult compileResult = compilerEngine.loadClass(expression);
            if (compileResult.getCode() != CompileResultCode.SUCCESS) {
                return ExpressionResult.failure(compileResult.getMsg(), engineType, expression);
            }

            Class<?> clazz = compileResult.getClazz();
            Object instance = clazz.getDeclaredConstructor().newInstance();

            // 尝试通过 execute(Map) 方法调用
            Method executeMethod = findExecuteMethod(clazz);
            if (executeMethod != null) {
                Map<String, Object> variables = context.getVariables();
                Object result = executeMethod.invoke(instance, variables);
                return ExpressionResult.success(result, engineType, expression, System.currentTimeMillis() - start);
            }

            // 尝试无参 main 方法
            Method mainMethod = findMainMethod(clazz);
            if (mainMethod != null) {
                Object result = mainMethod.invoke(instance);
                return ExpressionResult.success(result, engineType, expression, System.currentTimeMillis() - start);
            }

            return ExpressionResult.failure("No execute(Map) or main() method found in compiled class", engineType, expression);
        } catch (Exception e) {
            return ExpressionResult.failure(e.getMessage(), engineType, expression);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T evaluate(String expression, ExpressionContext context, Class<T> resultType) {
        ExpressionResult result = evaluate(expression, context);
        if (!result.isSuccess()) {
            throw new RuntimeException(engineType + " evaluation failed: " + result.getErrorMessage());
        }
        Object value = result.getValue();
        if (value == null) {
            return null;
        }
        if (resultType.isInstance(value)) {
            return (T) value;
        }
        throw new ClassCastException("Result type " + value.getClass().getName() + " cannot be cast to " + resultType.getName());
    }

    @Override
    public boolean evaluateBoolean(String expression, ExpressionContext context) {
        ExpressionResult result = evaluate(expression, context);
        if (!result.isSuccess()) {
            throw new RuntimeException(engineType + " boolean evaluation failed: " + result.getErrorMessage());
        }
        Object value = result.getValue();
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        return value != null;
    }

    @Override
    public boolean isValid(String expression) {
        try {
            CompileResult result = compilerEngine.loadClass(expression);
            return result.getCode() == CompileResultCode.SUCCESS;
        } catch (Exception e) {
            return false;
        }
    }

    private Method findExecuteMethod(Class<?> clazz) {
        try {
            return clazz.getMethod("execute", Map.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private Method findMainMethod(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if ("main".equals(method.getName()) && method.getParameterCount() == 0) {
                return method;
            }
        }
        return null;
    }
}
