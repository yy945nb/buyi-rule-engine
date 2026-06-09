package com.ymware.engine.controller;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Javadoc Provider interface for runtime javadoc access
 */
public interface JavadocProvider {

    String getClassJavadoc(Class<?> cl);

    Map<String, String> getRecordClassParamJavadoc(Class<?> cl);

    String getMethodJavadocDescription(Method method);

    String getMethodJavadocReturn(Method method);

    String getParamJavadoc(Method method, String name);

    String getFieldJavadoc(Field field);

    String getFirstSentence(String text);
}
