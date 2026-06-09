package com.ymware.engine.compiler.groovy;

import com.ymware.engine.compiler.CompilerEngine;
import com.ymware.engine.compiler.pojo.CompileResult;
import groovy.lang.GroovyClassLoader;

public class GroovyCompilerEngine implements CompilerEngine {

    private static final GroovyClassLoader loader = new GroovyClassLoader();

    @Override
    public CompileResult loadClass(String script) {
        try {
            Class<?> result = loader.parseClass(script);
            //这里比较简单，没有对代码进行reformat
            return CompileResult.success(result, script);
        } catch (Exception e) {
            return CompileResult.otherException(e);
        }
    }
}
