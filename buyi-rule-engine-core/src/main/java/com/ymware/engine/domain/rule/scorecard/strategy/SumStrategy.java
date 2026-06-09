package com.ymware.engine.domain.rule.scorecard.strategy;

import com.ymware.engine.domain.rule.service.Input;
import com.ymware.engine.config.RuleEngineConfiguration;
import com.ymware.engine.domain.rule.scorecard.Card;

import java.math.BigDecimal;
import java.util.List;

/**
 * 〈SumStrategy〉
 *
 * @author 丁乾文
 * @date 2019/8/13
 * @since 1.0.0
 */
public class SumStrategy implements ScoreCardStrategy {

    private static final SumStrategy INSTANCE = new SumStrategy();

    public static SumStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public BigDecimal compute(List<Card> cards, Input input, RuleEngineConfiguration configuration) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Card card : cards) {
            if (card.getConditionSet().compare(input, configuration)) {
                BigDecimal score = card.getScore();
                if (score != null) {
                    sum = sum.add(score);
                }
            }
        }
        return sum;
    }

}
