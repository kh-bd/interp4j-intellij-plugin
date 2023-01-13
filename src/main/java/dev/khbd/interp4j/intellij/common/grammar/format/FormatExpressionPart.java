package dev.khbd.interp4j.intellij.common.grammar.format;

/**
 * Format expressions can be represented as sequence of text and specifiers.
 *
 * @author Sergei_Khadanovich
 */
public sealed interface FormatExpressionPart permits FormatText, FormatSpecifier {
}
