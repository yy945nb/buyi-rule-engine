package com.ymware.engine.compiler.java.dependency;


import com.ymware.engine.compiler.pojo.JavaCompilerResult;

public interface Compiler {

    JavaCompilerResult compile(String script, ClassLoader classLoader);

}
