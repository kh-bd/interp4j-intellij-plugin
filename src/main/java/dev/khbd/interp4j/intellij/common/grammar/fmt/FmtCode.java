package dev.khbd.interp4j.intellij.common.grammar.fmt;

/**
 * Code part in s expression.
 *
 * <p>For example, in fmt("hello, %s$name"), expression part is name.
 *
 * @author Sergei_Khadanovich
 */
public record FmtCode(String expression, Position position) implements FmtExpressionPart {

    @Override
    public FmtExpressionPartKind kind() {
        return FmtExpressionPartKind.CODE;
    }

    @Override
    public void visit(FmtExpressionVisitor visitor) {
        visitor.visitCodePart(this);
    }
}
