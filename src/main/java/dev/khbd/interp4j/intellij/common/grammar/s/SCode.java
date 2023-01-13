package dev.khbd.interp4j.intellij.common.grammar.s;

/**
 * Code part in s expression.
 *
 * <p>For example, in s("hello, $name"), expression part is name.
 *
 * @author Sergei_Khadanovich
 */
public record SCode(String expression, int start, int end) implements SExpressionPart {
}
