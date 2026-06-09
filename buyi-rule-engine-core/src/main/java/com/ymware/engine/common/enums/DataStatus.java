package com.ymware.engine.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum DataStatus {

    /**
     * 规则/决策表的各种状态
     */
    DEV(0),

    TEST(1),

    PRD(2),
    /**
     * 历史的线上
     */
    HISTORY(3);

    private final Integer status;

}
