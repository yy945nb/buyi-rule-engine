package com.ymware.engine.compiler.pojo;

import lombok.Data;

import java.util.Map;

@Data
public class CompilerCache {
    private String mainClassName;
    private Map<String, String> encodeClassMap;
}
