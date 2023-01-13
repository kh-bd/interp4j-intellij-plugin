package dev.khbd.interp4j.intellij.common.grammar.s;

/**
 * 's' expression visitor.
 *
 * @author Sergei_Khadanovich
 */
public interface SExpressionVisitor {

    default void start() {
    }

    /**
     * Visit expression part.
     *
     * @param part expression part
     */
    default void visit(SExpressionPart part) {
        if (part instanceof SText text) {
            visitText(text);
        } else if (part instanceof SCode code) {
            visitCode(code);
        }
    }

    /**
     * Visit text part.
     *
     * @param text text part
     */
    default void visitText(SText text) {
    }

    /**
     * Visit code part.
     *
     * @param code part
     */
    default void visitCode(SCode code) {
    }

    default void finish() {
    }
}
