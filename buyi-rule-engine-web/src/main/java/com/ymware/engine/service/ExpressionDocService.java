package com.ymware.engine.service;

import com.ymware.engine.model.dto.doc.ExpressionDocDto;

import java.util.List;

/**
 * @author liukaixiong
 * @date 2024/11/12 - 16:51
 */
public interface ExpressionDocService {
    public ExpressionDocDto getKeyInfo(String serviceName, String name);

    public boolean refresh(String serviceName, List<ExpressionDocDto> variableInfoDto);

    List<ExpressionDocDto> getLikeName(String serviceName, String groupName, String name, Integer size);

}
