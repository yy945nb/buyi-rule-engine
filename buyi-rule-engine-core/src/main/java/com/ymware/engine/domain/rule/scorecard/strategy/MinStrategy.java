package com.ymware.engine.domain.rule.scorecard.strategy;

import com.ymware.engine.domain.rule.service.Input;
import com.ymware.engine.config.RuleEngineConfiguration;
import com.ymware.engine.domain.rule.scorecard.Card;

import java.math.BigDecimal;
import java.util.List;

/**
 * 〈MinStrategy〉
 *
 * @author 丁乾文
 * @date 2019/8/13
 * @since 1.0.0
 */
public class MinStrategy implements ScoreCardStrategy {

    private static final MinStrategy INSTANCE = new MinStrategy();

    public static MinStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public BigDecimal compute(List<Card> cards, Input input, RuleEngineConfiguration configuration) {
        BigDecimal min = null;
        for (Card card : cards) {
            if (card.getConditionSet().compare(input, configuration)) {
                BigDecimal score = card.getScore();
                if (score != null) {
                    if (min == null || score.compareTo(min) < 0) {
                        min = score;
                    }
                }
            }
        }
        return min != null ? min : BigDecimal.ZERO;
    }
}
