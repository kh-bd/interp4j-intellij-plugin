package dev.khbd.interp4j.intellij.common.grammar.format;

/**
 * Visitor interface to travers format expressions.
 *
 * @author Sergei_Khadanovich
 */
public interface FormatExpressionVisitor {

    default void start() {
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
    }

    default void finish() {
    }
}
