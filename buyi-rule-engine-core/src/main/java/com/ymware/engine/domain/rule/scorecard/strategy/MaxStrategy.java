package com.ymware.engine.domain.rule.scorecard.strategy;

import com.ymware.engine.domain.rule.service.Input;
import com.ymware.engine.config.RuleEngineConfiguration;
import com.ymware.engine.domain.rule.scorecard.Card;

import java.math.BigDecimal;
import java.util.List;

/**
 * 〈MaxStrategy〉
 *
 * @author 丁乾文
 * @date 2019/8/13
 * @since 1.0.0
 */
public class MaxStrategy implements ScoreCardStrategy{

    private static final MaxStrategy INSTANCE = new MaxStrategy();

    public static MaxStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public BigDecimal compute(List<Card> cards, Input input, RuleEngineConfiguration configuration) {
        BigDecimal max = null;
        for (Card card : cards) {
            if (card.getConditionSet().compare(input, configuration)) {
                BigDecimal score = card.getScore();
                if (score != null) {
                    if (max == null || score.compareTo(max) > 0) {
                        max = score;
                    }
                }
            }
        }
        return max != null ? max : BigDecimal.ZERO;
    }
}
