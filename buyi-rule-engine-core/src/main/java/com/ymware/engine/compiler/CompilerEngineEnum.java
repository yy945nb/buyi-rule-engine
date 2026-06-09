package com.ymware.engine.compiler;

import com.ymware.engine.compiler.groovy.GroovyCompilerEngine;
import com.ymware.engine.compiler.java.JavaCompilerEngine;
import com.ymware.engine.compiler.pojo.CompileResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum CompilerEngineEnum implements CompilerEngine {
    JAVA("JAVA", "java动态编译", new JavaCompilerEngine()),
    GROOVY("GROOVY", "groovy动态编译", new GroovyCompilerEngine()),
    ;
    private final String code;
    private final String description;
    private final CompilerEngine engine;

    @Override
    public CompileResult loadClass(String script) {
        return engine.loadClass(script);
    }
}
