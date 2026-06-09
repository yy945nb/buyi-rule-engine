package com.ymware.engine.expression.model.function;

import com.ymware.engine.annotation.PropertyDefinition;

public class TestUserModel {
    @PropertyDefinition(value = "用户名称")
    private String name;
    @PropertyDefinition(value = "年龄")
    private int age;
    @PropertyDefinition(value = "主键", required = true)
    private Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
