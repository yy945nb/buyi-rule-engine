package com.ymware.engine.service.impl;


import com.ymware.engine.enums.DataType;
import com.ymware.engine.common.exception.ApiException;
import com.ymware.engine.service.ImportExportService;
import com.ymware.engine.entity.RuleEngineGeneralRulePublish;
import com.ymware.engine.service.RuleEngineGeneralRulePublishManager;
import com.ymware.engine.vo.data.file.ExportRequest;
import com.ymware.engine.vo.data.file.ExportResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 〈ImportExportServiceImpl〉
 *
 * @author 丁乾文
 * @date 2021/7/13 5:41 下午
 * @since 1.0.0
 */
@Slf4j
@Service
public class ImportExportServiceImpl implements ImportExportService {

    @Resource
    private RuleEngineGeneralRulePublishManager ruleEngineGeneralRulePublishManager;

    /**
     * 导出文件 可执行json文件
     * <p>
     * *.r 为规则文件
     * *.dt 为决策表文件
     * *.rs 为规则集文件
     *
     * @return e
     */
    @Override
    public ExportResponse exportFile(ExportRequest exportRequest) {
        Long dataId = exportRequest.getDataId();
        String version = exportRequest.getVersion();
        DataType dataType = DataType.getByType(exportRequest.getDataType());
        ExportResponse exportResponse = new ExportResponse();
        exportResponse.setId(dataId);
        switch (dataType) {
            case GENERAL_RULE:
                RuleEngineGeneralRulePublish generalRulePublish = ruleEngineGeneralRulePublishManager.lambdaQuery()
                        .eq(RuleEngineGeneralRulePublish::getGeneralRuleId, dataId)
                        .eq(RuleEngineGeneralRulePublish::getVersion, version)
                        .one();
                if (generalRulePublish == null) {
                    throw new ApiException("没有找到可下载版本数据");
                }
                String data = generalRulePublish.getData();
                exportResponse.setData(data);
                // ...
                exportResponse.setCode(generalRulePublish.getGeneralRuleCode());
                break;
            case RULE_SET:
                break;
            case DECISION_TABLE:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dataType);
        }
        return exportResponse;
    }

}
