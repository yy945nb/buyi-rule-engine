package com.ymware.engine.domain.rule.model;

import lombok.Data;

import java.util.Map;

@Data
public class DataRow {

    private Map<String, DataColumn> columnMap;

}
