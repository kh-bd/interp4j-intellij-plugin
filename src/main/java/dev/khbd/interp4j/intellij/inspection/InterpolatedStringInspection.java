package dev.khbd.interp4j.intellij.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiMethodCallExpression;
import dev.khbd.interp4j.intellij.Interp4jBundle;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import dev.khbd.interp4j.processor.s.expr.ExpressionPart;
import dev.khbd.interp4j.processor.s.expr.SExpression;
import dev.khbd.interp4j.processor.s.expr.SExpressionParser;
import dev.khbd.interp4j.processor.s.expr.SExpressionVisitor;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolatedStringInspection extends LocalInspectionTool {

    @Override
    public PsiElementVisitor buildVisitor(ProblemsHolder holder, boolean isOnTheFly) {
        return new SMethodInvocationVisitor(holder);
    }

    @RequiredArgsConstructor
    static class SMethodInvocationVisitor extends PsiElementVisitor {

        private final ProblemsHolder holder;

        @Override
        public void visitElement(PsiElement element) {
            if (!(element instanceof PsiMethodCallExpression)) {
                return;
            }

            PsiMethodCallExpression methodCall = (PsiMethodCallExpression) element;
            if (!Interp4jPsiUtil.isSMethodCall(methodCall)) {
                return;
            }

            PsiExpressionList arguments = methodCall.getArgumentList();
            if (arguments.isEmpty()) {
                // maybe, user didn't complete typing
                // s function without arguments is a compile-time error
                return;
            }

            PsiExpression firstExpression = arguments.getExpressions()[0];
            String value = Interp4jPsiUtil.getStringLiteralValue(firstExpression);
            if (Objects.isNull(value)) {
                onlyStringLiteralValueIsSupported(firstExpression);
                return;
            }

            SExpressionParser.getInstance().parse(value)
                    .ifPresentOrElse(inspectParsed(methodCall), unpassableExpression());
        }

        private Consumer<SExpression> inspectParsed(PsiMethodCallExpression methodCall) {
            return sExpr -> {
                if (!existAnyExpressionPart(sExpr)) {
                    interpolationWithoutExpression(methodCall);
                }
            };
        }

        private boolean existAnyExpressionPart(SExpression sExpr) {
            ExpressionsCounter counter = new ExpressionsCounter();
            sExpr.visit(counter);
            return counter.count > 0;
        }

        private void interpolationWithoutExpression(PsiMethodCallExpression methodCall) {
            holder.registerProblem(
                    methodCall,
                    Interp4jBundle.getMessage("inspection.interpolated.string.no.one.expression.found"),
                    ProblemHighlightType.WARNING
            );
        }

        private Runnable unpassableExpression() {
            return () -> {
            };
        }

        private void onlyStringLiteralValueIsSupported(PsiExpression expression) {
            holder.registerProblem(
                    expression,
                    Interp4jBundle.getMessage("inspection.interpolated.string.is.not.string.literal"),
                    ProblemHighlightType.GENERIC_ERROR
            );
        }
    }

    private static class ExpressionsCounter implements SExpressionVisitor {

        private int count = 0;

        @Override
        public void visitExpressionPart(ExpressionPart expressionPart) {
            count++;
        }
    }
}
