package dev.khbd.interp4j.intellij.common.grammar.format;

/**
 * Text block in format expression.
 *
 * @author Sergei_Khadanovich
 */
public record FormatText(String text) implements FormatExpressionPart {

    public boolean isEmpty() {
        return text.isEmpty();
    }
}
