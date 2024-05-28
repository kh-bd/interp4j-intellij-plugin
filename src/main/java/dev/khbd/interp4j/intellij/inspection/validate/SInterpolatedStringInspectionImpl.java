package dev.khbd.interp4j.intellij.inspection.validate;

import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import dev.khbd.interp4j.intellij.common.grammar.s.SCode;
import dev.khbd.interp4j.intellij.common.grammar.s.SExpression;
import dev.khbd.interp4j.intellij.common.grammar.s.SExpressionParser;
import dev.khbd.interp4j.intellij.common.grammar.s.SExpressionVisitor;

/**
 * @author Sergei Khadanovich
 */
public class SInterpolatedStringInspectionImpl extends AbstractInterpolatedStringInspection<SExpression> {

    public SInterpolatedStringInspectionImpl() {
        super(Interp4jPsiUtil::isSMethodCall, literal -> SExpressionParser.getInstance().parse(literal), expr -> {
            NeedInterpolationVisitor visitor = new NeedInterpolationVisitor();
            expr.visit(visitor);
            return visitor.needInterpolation;
        });
    }

    private static class NeedInterpolationVisitor implements SExpressionVisitor {

        private boolean needInterpolation = false;

        @Override
        public void visitCode(SCode code) {
            needInterpolation = true;
        }
    }
}
