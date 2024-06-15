package dev.khbd.interp4j.intellij.common.grammar.fmt;

/**
 * Text block in format expression.
 *
 * @author Sergei Khadanovich
 */
public record FmtText(String text, Position position) implements FmtExpressionPart {

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
    boolean isEmpty() {
        return text.isEmpty();
    }
}
