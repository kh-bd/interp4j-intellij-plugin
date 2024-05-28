package dev.khbd.interp4j.intellij.inspection.validate;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiLiteralExpression;
import dev.khbd.interp4j.intellij.Interp4jBundle;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtCode;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtExpression;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtExpressionParser;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtExpressionPart;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtExpressionVisitor;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtSpecifier;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtText;
import dev.khbd.interp4j.intellij.common.grammar.fmt.Position;
import lombok.RequiredArgsConstructor;

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

    @Override
    public PsiElementVisitor buildVisitor(ProblemsHolder holder, boolean isOnTheFly) {
        return new FmtInterpolatorMethodInvocationVisitor(holder);
    }

    class FmtInterpolatorMethodInvocationVisitor extends InterpolatorMethodInvocationVisitor {

        FmtInterpolatorMethodInvocationVisitor(ProblemsHolder holder) {
            super(holder);
        }

        @Override
        protected void validate(PsiLiteralExpression literal, FmtExpression expression) {
            expression.visit(new FmtExpressionValidator(literal));
        }


        @RequiredArgsConstructor
        class FmtExpressionValidator implements FmtExpressionVisitor {

            final PsiLiteralExpression literal;

            FmtExpressionPart prev;

            @Override
            public void visitCodePart(FmtCode code) {
                if (prev == null) {
                    codeMustBeUsedAfterSpecifier(code);
                }

                if (prev != null) {
                    if (prev.isSpecifier()) {
                        FmtSpecifier specifier = (FmtSpecifier) prev;
                        if (specifier.isPercent() || specifier.isNewLine()) {
                            codeBeforeSpecialSpecifiers(code);
                        }
                    }
                    if (prev.isText()) {
                        codeMustBeUsedAfterSpecifier(code);
                    }
                }

                prev = code;
            }

            @Override
            public void visitSpecifierPart(FmtSpecifier specifier) {
                prev = specifier;
            }

            @Override
            public void visitTextPart(FmtText text) {
                if (prev != null && prev.isSpecifier()) {
                    FmtSpecifier specifier = (FmtSpecifier) prev;
                    if (!specifier.isNewLine() && !specifier.isPercent()) {
                        codeMustBeUsedAfterSpecifier(specifier);
                    }
                }

                prev = text;
            }

            @Override
            public void finish() {
                if (prev != null && prev.isSpecifier()) {
                    FmtSpecifier specifier = (FmtSpecifier) prev;
                    if (!specifier.isNewLine() && !specifier.isPercent()) {
                        codeMustBeUsedAfterSpecifier(specifier);
                    }
                }
            }

            private void codeMustBeUsedAfterSpecifier(FmtExpressionPart part) {
                Position position = part.getPosition();
                holder.registerProblem(
                        literal,
                        Interp4jBundle.getMessage("inspection.interpolated.string.fmt.code.must.be.used.after.specifier"),
                        ProblemHighlightType.GENERIC_ERROR,
                        new TextRange(position.getStart(), position.getEnd())
                );
            }

            private void codeBeforeSpecialSpecifiers(FmtCode code) {
                Position position = code.getPosition();
                holder.registerProblem(
                        literal,
                        Interp4jBundle.getMessage("inspection.interpolated.string.fmt.code.before.special.specifiers"),
                        ProblemHighlightType.GENERIC_ERROR,
                        new TextRange(position.getStart(), position.getEnd())
                );
            }
        }
    }

    private static class NeedInterpolationVisitor implements FmtExpressionVisitor {

        private boolean needInterpolation = false;

        @Override
        public void visitCodePart(FmtCode code) {
            needInterpolation = true;
        }

        @Override
        public void visitSpecifierPart(FmtSpecifier specifier) {
            needInterpolation = true;
        }
    }
}
