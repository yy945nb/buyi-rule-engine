package com.ymware.engine.controller;


import com.ymware.engine.constants.BaseConstants;
import com.ymware.engine.model.dto.request.AddExpressionNodeRequest;
import com.ymware.engine.model.dto.request.DeleteByIdListRequest;
import com.ymware.engine.model.request.EditExpressionNodeRequest;
import com.ymware.engine.model.dto.request.QueryExpressionNodeRequest;
import com.ymware.engine.model.response.ExpressionNodeDTO;
import com.ymware.engine.model.response.RestResult;
import com.ymware.engine.service.ExpressionKeyService;
import com.ymware.engine.service.ExpressionNodeConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 引擎节点信息 前端控制器
 * </p>
 *
 * @author bsy
 * @since 2022-06-08
 */
@Tag(name = "节点管理")
@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(BaseConstants.BASE_PATH + "/node")
@Slf4j
@Deprecated
public class NodeConfigController {

    @Autowired
    private ExpressionNodeConfigService nodeConfigService;

    @Autowired
    private ExpressionKeyService keyService;

    @Operation(summary = "添加单个服务节点")
    @PostMapping("/addOne")
    public RestResult<ExpressionNodeDTO> addOne(@Validated @RequestBody AddExpressionNodeRequest addExpressionNodeRequest) {
        return nodeConfigService.addExpressionNode(addExpressionNodeRequest);
    }

    @Operation(summary = "编辑单个服务节点")
    @PostMapping("/editOne")
    public RestResult<ExpressionNodeDTO> editOne(@RequestBody @Validated EditExpressionNodeRequest editRequest) {
        return nodeConfigService.editExpressionNode(editRequest);
    }

    @Operation(summary = "查询服务节点")
    @PostMapping("/findNode")
    public RestResult<List<ExpressionNodeDTO>> findNodeList(@RequestBody QueryExpressionNodeRequest queryRequest) {
        return nodeConfigService.queryExpressionNode(queryRequest);
    }

    @Operation(summary = "批量逻辑删除服务节点")
    @PostMapping("/batchDelete")
    public RestResult<?> batchDelete(@RequestBody @Validated DeleteByIdListRequest delRequest) {
        return nodeConfigService.batchDeleteByIdList(delRequest);
    }

//    @Operation(summary = "节点数据刷新")
//    @GetMapping("/refreshKey")
//    public RestResult<?> refresh(@RequestParam(name = "serviceName") String serviceName) {
//        try {
//            boolean result = keyService.refreshDocument(serviceName);
//            log.debug("更新节点关键字:{} -> {}", serviceName, result);
//            return RestResult.ok();
//        } catch (Exception e) {
//            return RestResult.failed("节点数据更新失败:" + e.getMessage());
//        }
//
//    }
}
