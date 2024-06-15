package dev.khbd.interp4j.intellij.common.grammar.fmt;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Format expression builder.
 *
 * @author Sergei Khadanovich
 */
public class FmtExpressionBuilder {

    private final List<FmtExpressionPart> parts = new ArrayList<>();

    FmtExpressionBuilder() {
    }

    /**
     * Add text part to builder.
     *
     * @param text text part
     */
    public FmtExpressionBuilder text(@NonNull FmtText text) {
        this.parts.add(text);
        return this;
    }

    /**
     * Add code part to builder.
     *
     * @param code code part
     */
    public FmtExpressionBuilder code(@NonNull FmtCode code) {
        this.parts.add(code);
        return this;
    }

    /**
     * Add specifier part to builder.
     *
     * @param specifier specifier part
     */
    public FmtExpressionBuilder specifier(@NonNull FmtSpecifier specifier) {
        this.parts.add(specifier);
        return this;
    }

    /**
     * Create format expression.
     */
    public FmtExpression build() {
        return new FmtExpression(parts);
    }
}
