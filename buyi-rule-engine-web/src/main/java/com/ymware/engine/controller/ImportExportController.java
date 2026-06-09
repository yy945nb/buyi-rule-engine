package com.ymware.engine.controller;

import com.ymware.engine.common.vo.PlainResult;
import com.ymware.engine.service.ImportExportService;
import com.ymware.engine.vo.data.file.ExportRequest;
import com.ymware.engine.vo.data.file.ExportResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 〈ImportExportController〉
 *
 * @author 丁乾文
 * @date 2021/6/18 2:42 下午
 * @since 1.0.0
 */
@Tag(name = "导入导出控制器")
@RestController
@Validated
@RequestMapping("ruleEngine/importExport")
public class ImportExportController {

    @Resource
    private ImportExportService importExportService;

    /**
     * 导出文件 可执行json文件
     * <p>
     * *.r 为规则文件
     * *.dt 为决策表文件
     * *.rs 为规则集文件
     */
    @PostMapping("export")
    @Operation(summary = "导出文件")
    public PlainResult<ExportResponse> exportFile(@RequestBody @Valid ExportRequest exportRequest) {
        PlainResult<ExportResponse> plainResult = new PlainResult<>();
        plainResult.setData(importExportService.exportFile(exportRequest));
        return plainResult;
    }

    /**
     * 导入文件 可执行json文件
     * <p>
     * *.r 为规则文件
     * *.dt 为决策表文件
     * *.rs 为规则集文件
     */
    @PostMapping("import")
    @Operation(summary = "导入文件")
    public void importFile() {

    }

}
