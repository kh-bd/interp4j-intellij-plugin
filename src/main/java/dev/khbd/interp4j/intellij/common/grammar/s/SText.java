package dev.khbd.interp4j.intellij.common.grammar.s;

/**
 * Text part in s expression.
 *
 * <p>For example, in s("hello, $name"), text part is "hello ".
 *
 * @author Sergei_Khadanovich
 */
public record SText(String text, int start, int end) implements SExpressionPart {
}
