package com.ymware.engine.vo.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Administrator
 */
@Data
public class HistoryListResponse {

    private Long id;

    private Long dataId;

    private String code;

    private String name;

    private String version;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
