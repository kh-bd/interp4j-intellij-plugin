package dev.khbd.interp4j.intellij.inspection.validate;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import dev.khbd.interp4j.intellij.Interp4jBundle;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import lombok.RequiredArgsConstructor;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateMethodInvocationInspection extends LocalInspectionTool {

    @Override
    public PsiElementVisitor buildVisitor(ProblemsHolder holder, boolean isOnTheFly) {
        return new InterpolateMethodInvocationVisitor(holder);
    }

    @RequiredArgsConstructor
    static class InterpolateMethodInvocationVisitor extends PsiElementVisitor {

        private final ProblemsHolder holder;

        @Override
        public void visitElement(PsiElement element) {
            if (!(element instanceof PsiMethodCallExpression)) {
                return;
            }

            PsiMethodCallExpression methodCall = (PsiMethodCallExpression) element;
            if (Interp4jPsiUtil.isInterpolateMethodCall(methodCall)) {
                interpolateMethodCallProblem(methodCall);
            }
        }

        private void interpolateMethodCallProblem(PsiMethodCallExpression methodCall) {
            holder.registerProblem(
                    methodCall,
                    Interp4jBundle.getMessage("inspection.interpolate.method.invocation.detected"),
                    ProblemHighlightType.WARNING
            );
        }
    }
}
