package dev.khbd.interp4j.intellij.common.grammar.fmt;

/**
 * 'fmt' expression visitor.
 *
 * @author Sergei_Khadanovich
 */
public interface FmtExpressionVisitor {

    /**
     * Start traversing.
     */
    default void start() {
    }

    /**
     * Visit text part.
     *
     * @param text text part
     */
    default void visitTextPart(FmtText text) {
    }

    /**
     * Visit code part.
     *
     * @param code code part
     */
    default void visitCodePart(FmtCode code) {
    }

    /**
     * Visit specifier part.
     *
     * @param specifier specifier part
     */
    default void visitSpecifierPart(FmtSpecifier specifier) {
    }

    /**
     * Finish traversing.
     */
    default void finish() {
    }
}
