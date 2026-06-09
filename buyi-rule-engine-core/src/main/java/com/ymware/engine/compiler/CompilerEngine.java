package com.ymware.engine.compiler;


import com.ymware.engine.compiler.pojo.CompileResult;

public interface CompilerEngine {
    CompileResult loadClass(String script);
}
