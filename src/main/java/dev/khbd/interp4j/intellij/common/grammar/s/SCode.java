package dev.khbd.interp4j.intellij.common.grammar.s;

import lombok.Value;

/**
 * Code part in s expression.
 *
 * <p>For example, in s("hello, $name"), expression part is name.
 *
 * @author Sergei_Khadanovich
 */
@Value
public class SCode implements SExpressionPart {

    String expression;
    int start;
    int end;
}
