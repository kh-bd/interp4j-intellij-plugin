package dev.khbd.interp4j.intellij.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethodCallExpression;
import dev.khbd.interp4j.intellij.Interp4jBundle;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

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

            PsiExpression argument = arguments.getExpressions()[0];
            if (!(argument instanceof PsiLiteralExpression)) {
                onlyStringLiteralValueIsSupported(argument);
                return;
            }

            PsiLiteralExpression literalExpression = (PsiLiteralExpression) argument;

            Object value = literalExpression.getValue();
            if (Objects.isNull(value)) {
                onlyStringLiteralValueIsSupported(literalExpression);
                return;
            }

            if (!(value instanceof String)) {
                onlyStringLiteralValueIsSupported(literalExpression);
            }
        }

        private void onlyStringLiteralValueIsSupported(PsiExpression expression) {
            holder.registerProblem(expression,
                    Interp4jBundle.getMessage("inspection.interpolated.string.is.not.string.literal"),
                    ProblemHighlightType.GENERIC_ERROR
            );
        }
    }
}
