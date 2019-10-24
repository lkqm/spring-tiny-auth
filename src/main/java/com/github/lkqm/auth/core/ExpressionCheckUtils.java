package com.github.lkqm.auth.core;

import lombok.experimental.UtilityClass;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@UtilityClass
public class ExpressionCheckUtils {
    private static ExpressionParser PARSER = new SpelExpressionParser();

    /**
     * 校验expression是否能通过rootObject的检测
     *
     * @param context    上下文
     * @param expression 表达式
     * @return 是否通过
     */
    public static boolean check(EvaluationContext context, String expression) {
        Boolean result = PARSER.parseExpression(expression)
                .getValue(context, Boolean.class);
        return result != null ? result : false;
    }
}