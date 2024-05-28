package dev.khbd.interp4j.intellij.common.grammar.format;

import lombok.Value;

/**
 * Text block in format expression.
 *
 * @author Sergei_Khadanovich
 */
@Value
public class FormatText implements FormatExpressionPart {

    String text;

    public boolean isEmpty() {
        return text.isEmpty();
    }
}
