package com.ymware.engine.domain.rule.scorecard;
import com.ymware.engine.config.RuleEngineConfiguration;

import cn.hutool.core.collection.CollUtil;
import com.ymware.engine.domain.rule.service.Input;
import com.ymware.engine.domain.rule.service.RuleDataSupport;
import com.ymware.engine.domain.rule.service.JsonParse;
import com.ymware.engine.domain.value.model.Parameter;
import com.ymware.engine.domain.rule.scorecard.strategy.ScoreCardStrategy;
import com.ymware.engine.domain.rule.scorecard.strategy.ScoreCardStrategyFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈ScoreCard〉
 * <p>
 * 1：直接返回对应的策略分值
 * 2：可以进行别的业务逻辑判断
 *
 * @author 丁乾文
 * @date 2019/8/13
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class ScoreCard extends RuleDataSupport implements JsonParse {

    /**
     * 规则集
     */
    private List<Card> cards = new ArrayList<>();

    private ScoreCardStrategyType scoreCardStrategyType = ScoreCardStrategyType.SUM;

    /**
     * 参数
     * <p>
     * key: 组名称
     * value：组内所有参数
     * <p>
     * 例如key：user 则value存放： name、age等参数
     */
    private Map<String, Parameter> parameterMap = new HashMap<>();


    public ScoreCard() {

    }

    @Nullable
    @Override
    protected BigDecimal execute(@NonNull Input input, @NonNull RuleEngineConfiguration configuration) {
        if (CollUtil.isEmpty(this.cards)) {
            return Card.ZERO;
        }
        ScoreCardStrategy scoreCardStrategy = ScoreCardStrategyFactory.getInstance(this.scoreCardStrategyType);
        return scoreCardStrategy.compute(this.cards, input, configuration);
    }


}
