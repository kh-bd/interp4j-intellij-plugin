package dev.khbd.interp4j.intellij.common.grammar.s;

import lombok.Value;

/**
 * Text part in s expression.
 *
 * <p>For example, in s("hello, $name"), text part is "hello ".
 *
 * @author Sergei_Khadanovich
 */
@Value
public class SText implements SExpressionPart {

    String text;
    int start;
    int end;

    public boolean isEmpty() {
        return text.isEmpty();
    }
}
