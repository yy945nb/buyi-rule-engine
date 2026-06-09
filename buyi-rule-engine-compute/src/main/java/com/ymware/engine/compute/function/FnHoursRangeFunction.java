package com.ymware.engine.compute.function;

import cn.hutool.core.date.DateUtil;
import com.ymware.engine.compute.api.ExpressFunctionDocumentLoader;
import com.ymware.engine.compute.engine.ExpressionEnvContext;
import com.ymware.engine.compute.process.AbstractSimpleFunction;
import com.ymware.engine.model.request.ExpressionBaseRequest;
import com.ymware.engine.model.ExpressionConfigTreeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统时间小时范围
 *
 * @author liukaixiong
 * @date 2024/8/15 - 11:44
 */
@Component
@Slf4j
public class FnHoursRangeFunction extends AbstractSimpleFunction {

    @Override
    public Object processor(ExpressionEnvContext env, ExpressionConfigTreeModel configTreeModel, ExpressionBaseRequest request, List<Object> funArgs) {

        final Long startHour = getArgsIndexValue(funArgs, 0);

        final Long endHour = getArgsIndexValue(funArgs, 1);

        final int currentHour = DateUtil.thisHour(true);

        final boolean result = startHour < currentHour && currentHour < endHour;

        if (!result) {
            env.recordTraceDebugContent(getName(), "debug", String.format("startHour=%s;endHour=%s;currentHour=%s", startHour, endHour, currentHour));
            log.info("系统时间不在指定小时时间范围内,startHour={};endHour={};currentHour={}", startHour, endHour, currentHour);
        }
        return result;
    }

    @Override
    public Enum<? extends ExpressFunctionDocumentLoader> documentRegister() {
        return BaseFunctionDescEnum.SYS_DATE_HOUR_RANGE;
    }
}
