package dev.khbd.interp4j.intellij.common.grammar.s;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
@ToString
@EqualsAndHashCode
public class SExpression  {

    private final List<SExpressionPart> parts;

    SExpression() {
        this.parts = new ArrayList<>();
    }

    SExpression addPart(SExpressionPart part) {
        this.parts.add(part);
        return this;
    }

    /**
     * Visit s expression.
     *
     * @param visitor visitor
     */
    public void visit(SExpressionVisitor visitor) {
        visitor.start();
        for (SExpressionPart part : parts) {
            visitor.visit(part);
        }
        visitor.finish();
    }

}
