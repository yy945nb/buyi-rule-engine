package com.ymware.engine.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 标签定义表
 * </p>
 *
 * @author sanyuan
 * @since 2025-11-28
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "TagDefinitionDTO对象 - 标签定义表")
public class TagDefinitionDto  {

    private Long id;

    @Schema(description = "标签名称")
    private String tagName;

    @Schema(description = "标签编码")
    private String tagCode;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "数据类型")
    private String dataType;

    @Schema(description = "默认值")
    private String defaultValue;

    @Schema(description = "取值范围")
    private String valueRange;

    @Schema(description = "是否必填")
    private Boolean required;

    @Schema(description = "是否多选")
    private Boolean multi;

    @Schema(description = "标签描述")
    private String description;

    @Schema(description = "标签颜色")
    private String color;

    @Schema(description = "状态：1启用 0禁用")
    private Boolean status;

    @Schema(description = "业务规则列表")
    private List<TagBusinessRuleDto> tagBusinessRules;

}

