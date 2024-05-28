package dev.khbd.interp4j.intellij.common.grammar.fmt;

import lombok.Value;

/**
 * Code part in s expression.
 *
 * <p>For example, in fmt("hello, %s$name"), expression part is name.
 *
 * @author Sergei_Khadanovich
 */
@Value
public class FmtCode implements FmtExpressionPart {

    String expression;
    Position position;

    @Override
    public FmtExpressionPartKind kind() {
        return FmtExpressionPartKind.CODE;
    }

    @Override
    public void visit(FmtExpressionVisitor visitor) {
        visitor.visitCodePart(this);
    }
}
