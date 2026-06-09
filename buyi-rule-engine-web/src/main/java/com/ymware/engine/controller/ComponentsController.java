package com.ymware.engine.controller;


import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import com.ymware.engine.components.SyncDataManager;
import com.ymware.engine.constants.BaseConstants;
import com.ymware.engine.enums.SyncDataEnums;
import com.ymware.engine.entity.ExpressionExecutorBaseInfo;
import com.ymware.engine.model.response.RestResult;
import com.ymware.engine.service.impl.ExpressionConfigSyncDataServiceImpl;
import com.ymware.engine.model.dto.sync.BaseSyncData;
import com.ymware.engine.model.dto.sync.ExpressionExecutorSyncData;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author bsy
 * @since 2022-06-12
 */
@Tag(name = "通用模块")
@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(BaseConstants.BASE_PATH + "/components")
public class ComponentsController {
    @Autowired
    private ExpressionConfigSyncDataServiceImpl expressionConfigSyncDataService;

    @Autowired
    private SyncDataManager syncDataManager;

    @Operation(summary = "刷新执行器相关数据")
    @GetMapping("/syncTest")
    public RestResult<?> syncTest(@RequestParam("id") Long executorId) {
        ExpressionExecutorSyncData export = expressionConfigSyncDataService.export(executorId);
        ExpressionExecutorBaseInfo baseInfo = export.getBaseInfo();
        baseInfo.setBusinessCode("test_copy_insert");
        expressionConfigSyncDataService.importData(export);
//        eventPublisher.publishEvent(new ExecutorConfigRefreshEvent(executorId));
        return RestResult.ok();
    }

    @Operation(summary = "导出对应的数据集合")
    @GetMapping("/exportData")
    public ResponseEntity<?> exportData(@RequestParam("type") SyncDataEnums syncDataEnums, @RequestParam("exportId") Long exportId) {
        BaseSyncData export = syncDataManager.export(syncDataEnums, exportId);
        String fileName = String.format("export_%s_%s_%s.json", syncDataEnums.name().toLowerCase(), exportId, DatePattern.PURE_DATE_FORMAT.format(new Date()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .body(export);
    }

    @Operation(summary = "导入对应数据")
    @PostMapping("/importData")
    public RestResult<?> importData(@RequestParam("file") MultipartFile multipartFile) {
        try {
            InputStream inputStream = multipartFile.getInputStream();
            BaseSyncData baseSyncData = JSON.parseObject(inputStream, BaseSyncData.class);
            return RestResult.ok(syncDataManager.importData(baseSyncData));
        } catch (IOException e) {
            e.printStackTrace();
            return RestResult.failed("导入失败,文件有误!");
        }
    }

}
