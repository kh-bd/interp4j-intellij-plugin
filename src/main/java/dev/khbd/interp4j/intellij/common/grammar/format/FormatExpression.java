package dev.khbd.interp4j.intellij.common.grammar.format;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Format expression.
 *
 * <p>Expression represents parsed string literal which
 * is used in {@link String#format(String, Object...)} invocation.
 *
 * @author Sergei_Khadanovich
 */
@EqualsAndHashCode
@ToString
public class FormatExpression {

    private final List<FormatExpressionPart> parts;

    FormatExpression() {
        parts = new ArrayList<>();
    }

    /**
     * Add part to last position in expression.
     *
     * @param part part
     * @return self
     */
    FormatExpression addPart(@NonNull FormatExpressionPart part) {
        parts.add(part);
        return this;
    }

    /**
     * Traverse self with specified visitor.
     *
     * @param visitor visitor
     */
    public void visit(FormatExpressionVisitor visitor) {
        visitor.start();
        for (FormatExpressionPart part : parts) {
            if (part instanceof FormatSpecifier specifier) {
                visitor.visitSpecifier(specifier);
            } else if (part instanceof FormatText text) {
                visitor.visitText(text);
            }
        }
        visitor.finish();
    }

    /**
     * Calculate specifiers count.
     */
    public long specifiersCount() {
        return parts.stream()
                .filter(part -> part instanceof FormatSpecifier)
                .count();
    }
}
