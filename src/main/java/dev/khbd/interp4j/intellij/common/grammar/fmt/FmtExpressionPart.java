package dev.khbd.interp4j.intellij.common.grammar.fmt;

/**
 * Format expression part model.
 *
 * @author Sergei Khadanovich
 */
public interface FmtExpressionPart {

    /**
     * Get expression part kind.
     */
    FmtExpressionPartKind kind();

    /**
     * Visit expression part.
     */
    void visit(FmtExpressionVisitor visitor);

    /**
     * Get part position.
     */
    Position getPosition();

    /**
     * Is expression part text or not.
     *
     * @return {@literal true} if expression part is text and {@literal false} otherwise
     */
    default boolean isText() {
        return kind() == FmtExpressionPartKind.TEXT;
    }

    /**
     * Is expression part specifier or not.
     *
     * @return {@literal true} if expression part is specifier and {@literal false} otherwise
     */
    default boolean isSpecifier() {
        return kind() == FmtExpressionPartKind.SPECIFIER;
    }

    /**
     * Is expression part code or not.
     *
     * @return {@literal true} if expression part is code and {@literal false} otherwise
     */
    default boolean isCode() {
        return kind() == FmtExpressionPartKind.CODE;
    }
}
