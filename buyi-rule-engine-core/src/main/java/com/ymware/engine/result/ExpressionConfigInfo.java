package com.ymware.engine.result;

import com.ymware.engine.model.ExpressionConfigTreeModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpressionConfigInfo {

    private Long executorId;

    private String serviceName;

    private String businessCode;

    private String executorCode;

    private String executorName;

    private String varDefinition;

    /**
     * 拓展能力
     */
    private Map<String, Object> configurabilityMap;

    private List<ExpressionConfigTreeModel> configTreeModelList;

    private Long timestamp;

}
