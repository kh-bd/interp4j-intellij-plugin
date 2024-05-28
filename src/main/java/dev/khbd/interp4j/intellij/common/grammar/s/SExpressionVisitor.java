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
        if (part instanceof SText) {
            visitText((SText) part);
        } else if (part instanceof SCode) {
            visitCode((SCode) part);
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
