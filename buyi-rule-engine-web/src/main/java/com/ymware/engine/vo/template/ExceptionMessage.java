package com.ymware.engine.vo.template;

import com.ymware.engine.utils.TraceInterceptor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2024/9/18
 * @since 1.0.0
 */
@Data
public class ExceptionMessage {

    private String type;

    private LocalDateTime time = LocalDateTime.now();

    private String message;

    private String requestId = TraceInterceptor.getRequestId();

}
