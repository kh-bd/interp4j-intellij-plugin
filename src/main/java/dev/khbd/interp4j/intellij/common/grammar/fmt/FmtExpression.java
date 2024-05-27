package dev.khbd.interp4j.intellij.common.grammar.fmt;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Format expression model.
 *
 * @author Sergei Khadanovich
 */
@ToString
@EqualsAndHashCode
public class FmtExpression {

    private final List<FmtExpressionPart> parts;

    public FmtExpression(List<FmtExpressionPart> parts) {
        this.parts = new ArrayList<>(parts);
    }

    /**
     * Visit fmt expression.
     *
     * @param visitor visitor
     */
    public void visit(FmtExpressionVisitor visitor) {
        visitor.start();
        for (FmtExpressionPart part : parts) {
            part.visit(visitor);
        }
        visitor.finish();
    }

    /**
     * Create empty builder.
     *
     * @return builder
     */
    public static FmtExpressionBuilder builder() {
        return new FmtExpressionBuilder();
    }
}
