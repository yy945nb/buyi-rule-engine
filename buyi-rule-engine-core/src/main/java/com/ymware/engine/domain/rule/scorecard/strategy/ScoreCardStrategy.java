package com.ymware.engine.domain.rule.scorecard.strategy;

import com.ymware.engine.domain.rule.service.Input;
import com.ymware.engine.config.RuleEngineConfiguration;
import com.ymware.engine.domain.rule.scorecard.Card;

import java.math.BigDecimal;
import java.util.List;

/**
 * 〈ScoreCardStrategy〉
 *
 * @author 丁乾文
 * @date 2019/8/13
 * @since 1.0.0
 */
public interface ScoreCardStrategy {

    BigDecimal compute(List<Card> cards, Input input, RuleEngineConfiguration configuration);
}
