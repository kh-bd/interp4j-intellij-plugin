package dev.khbd.interp4j.intellij.common.grammar.format;

/**
 * Visitor interface to travers format expressions.
 *
 * @author Sergei_Khadanovich
 */
public interface FormatExpressionVisitor {

    default void start() {
    }

    default void visitPart(FormatExpressionPart part) {
        if (part instanceof FormatText text) {
            visitText(text);
        } else if (part instanceof FormatSpecifier specifier) {
            visitSpecifier(specifier);
        }
    }

    /**
     * Visit text part.
     */
    default void visitText(FormatText text) {
    }

    /**
     * Visit specifier part.
     */
    default void visitSpecifier(FormatSpecifier specifier) {
        visitIndex(specifier.index());
        visitFlags(specifier.flags());
        visitWidth(specifier.width());
        visitPrecision(specifier.precision());
        visitConversion(specifier.conversion());
    }

    /**
     * Visit specifier's index.
     */
    default void visitIndex(Index index) {
        if (index instanceof NumericIndex numericIndex) {
            visitNumericIndex(numericIndex);
        } else if (index instanceof ImplicitIndex implicitIndex) {
            visitImplicitIndex(implicitIndex);
        }
    }

    /**
     * Visit specifier's index.
     */
    default void visitNumericIndex(NumericIndex index) {
    }

    /**
     * Visit specifier's index.
     */
    default void visitImplicitIndex(ImplicitIndex index) {
    }

    /**
     * Visit specifier's flags.
     */
    default void visitFlags(String flag) {
    }

    /**
     * Visit specifier's width.
     */
    default void visitWidth(Integer width) {
    }

    /**
     * Visit specifier's precision.
     */
    default void visitPrecision(Integer precision) {
    }

    /**
     * Visit specifier's conversion.
     */
    default void visitConversion(Conversion conversion) {
    }

    default void finish() {
    }
}
