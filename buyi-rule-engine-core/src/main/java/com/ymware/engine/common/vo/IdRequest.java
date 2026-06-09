package com.ymware.engine.common.vo;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class IdRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id cannot be null")
    private Long id;
}
