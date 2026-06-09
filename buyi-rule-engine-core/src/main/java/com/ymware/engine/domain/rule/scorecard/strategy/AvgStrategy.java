package com.ymware.engine.domain.rule.scorecard.strategy;

import com.ymware.engine.domain.rule.service.Input;
import com.ymware.engine.config.RuleEngineConfiguration;
import com.ymware.engine.domain.rule.scorecard.Card;

import java.math.BigDecimal;
import java.util.List;

/**
 * 〈AvgStrategy〉
 *
 * @author 丁乾文
 * @date 2019/8/13
 * @since 1.0.0
 */
public class AvgStrategy implements ScoreCardStrategy {

    private static final AvgStrategy INSTANCE = new AvgStrategy();

    public static AvgStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public BigDecimal compute(List<Card> cards, Input input, RuleEngineConfiguration configuration) {
        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;
        for (Card card : cards) {
            if (card.getConditionSet().compare(input, configuration)) {
                BigDecimal score = card.getScore();
                if (score != null) {
                    sum = sum.add(score);
                    count++;
                }
            }
        }
        if (count == 0) {
            return BigDecimal.ZERO;
        }
        return sum.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);
    }

}
