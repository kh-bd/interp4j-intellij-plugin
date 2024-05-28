package dev.khbd.interp4j.intellij.inspection.validate;

import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtCode;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtExpression;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtExpressionParser;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtExpressionVisitor;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtSpecifier;

/**
 * @author Sergei Khadanovich
 */
public class FmtInterpolatedStringInspectionImpl extends AbstractInterpolatedStringInspection<FmtExpression> {

    public FmtInterpolatedStringInspectionImpl() {
        super(Interp4jPsiUtil::isFmtMethodCall, literal -> FmtExpressionParser.getInstance().parse(literal), expr -> {
            NeedInterpolationVisitor visitor = new NeedInterpolationVisitor();
            expr.visit(visitor);
            return visitor.needInterpolation;
        });
    }

    private static class NeedInterpolationVisitor implements FmtExpressionVisitor {

        private boolean needInterpolation = false;

        @Override
        public void visitCodePart(FmtCode code) {
            needInterpolation = true;
        }

        @Override
        public void visitSpecifierPart(FmtSpecifier specifier) {
            needInterpolation |= specifier.isPercent() || specifier.isNewLine();
        }
    }
}
