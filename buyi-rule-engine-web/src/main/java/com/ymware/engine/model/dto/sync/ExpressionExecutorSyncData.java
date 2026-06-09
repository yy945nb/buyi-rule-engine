package com.ymware.engine.model.dto.sync;

import com.ymware.engine.entity.ExpressionExecutorBaseInfo;
import com.ymware.engine.entity.ExpressionExecutorInfoConfig;
import lombok.Data;

import java.util.List;

/**
 * @author liukaixiong
 * @date 2024/2/20
 */
@Data
public class ExpressionExecutorSyncData {


    private ExpressionExecutorBaseInfo baseInfo;

    private List<ExpressionExecutorInfoConfig> nodeInfo;

}
