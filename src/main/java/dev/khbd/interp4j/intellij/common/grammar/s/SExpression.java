package dev.khbd.interp4j.intellij.common.grammar.s;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
@ToString
@EqualsAndHashCode
public class SExpression implements Iterable<SExpressionPart> {

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

    @Override
    public Iterator<SExpressionPart> iterator() {
        // Don't use ArrayList#iterator, because underling implementation supports
        // Iterator#remove, but our structure is immutable from outside
        Iterator<SExpressionPart> iterator = parts.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public SExpressionPart next() {
                return iterator.next();
            }
        };
    }

    public boolean hasAnyCode() {
        return parts.stream().anyMatch(part -> part instanceof SCode);
    }
}
