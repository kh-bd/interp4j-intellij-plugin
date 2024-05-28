package dev.khbd.interp4j.intellij.common.grammar.fmt;

import lombok.Value;

/**
 * Text block in format expression.
 *
 * @author Sergei Khadanovich
 */
@Value
public class FmtText implements FmtExpressionPart {

    String text;
    Position position;

    @Override
    public FmtExpressionPartKind kind() {
        return FmtExpressionPartKind.TEXT;
    }

    @Override
    public void visit(FmtExpressionVisitor visitor) {
        visitor.visitTextPart(this);
    }

    /**
     * Check if text block is empty or not.
     */
    public boolean isEmpty() {
        return text.isEmpty();
    }
}
