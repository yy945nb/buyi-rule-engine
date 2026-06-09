package com.ymware.engine.common.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

/**
 * Page response wrapper
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private long current;
    private long size;
    private long total;
}
