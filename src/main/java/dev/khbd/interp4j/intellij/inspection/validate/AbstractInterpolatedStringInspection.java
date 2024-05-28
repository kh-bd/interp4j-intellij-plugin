package dev.khbd.interp4j.intellij.inspection.validate;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.undo.UndoUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiPolyadicExpression;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.tree.IElementType;
import dev.khbd.interp4j.intellij.Interp4jBundle;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Sergei_Khadanovich
 */
@RequiredArgsConstructor
public abstract class AbstractInterpolatedStringInspection<E> extends LocalInspectionTool {

    private final Predicate<PsiMethodCallExpression> callPredicate;
    private final Function<String, Optional<E>> parser;
    private final Function<E, Boolean> needInterpolationPredicate;

    @Override
    public PsiElementVisitor buildVisitor(ProblemsHolder holder, boolean isOnTheFly) {
        return new InterpolatorMethodInvocationVisitor(holder);
    }

    @RequiredArgsConstructor
    protected class InterpolatorMethodInvocationVisitor extends PsiElementVisitor {

        protected final ProblemsHolder holder;

        @Override
        public void visitElement(PsiElement element) {
            if (!(element instanceof PsiMethodCallExpression)) {
                return;
            }

            PsiMethodCallExpression methodCall = (PsiMethodCallExpression) element;

            if (!callPredicate.test(methodCall)) {
                return;
            }

            PsiExpressionList arguments = methodCall.getArgumentList();
            if (arguments.isEmpty()) {
                // maybe, user didn't complete typing
                // interpolator function without arguments is a compile-time error
                return;
            }

            inspectExpression(methodCall, arguments.getExpressions()[0]);
        }

        /**
         * Validate expression for correctness.
         */
        protected void validate(PsiLiteralExpression literal, E expression) {
        }

        private void inspectExpression(PsiMethodCallExpression methodCall, PsiExpression expression) {
            if (expression instanceof PsiLiteralExpression) {
                inspectLiteralExpression(methodCall, (PsiLiteralExpression) expression);
            } else if (expression instanceof PsiPolyadicExpression) {
                inspectPolyadicExpression(methodCall, (PsiPolyadicExpression) expression);
            } else {
                wrongExpressionType(expression);
            }
        }

        private void inspectLiteralExpression(PsiMethodCallExpression methodCall,
                                              PsiLiteralExpression literalExpr) {
            String value = Interp4jPsiUtil.getStringLiteralText(literalExpr);
            if (Objects.isNull(value)) {
                wrongExpressionType(literalExpr);
                return;
            }

            parser.apply(value).ifPresentOrElse(inspectParsed(methodCall, literalExpr), () -> unpassableExpression(literalExpr));
        }

        private void inspectPolyadicExpression(PsiMethodCallExpression methodCall, PsiPolyadicExpression polyExpr) {
            if (!isPlusPolyExpression(polyExpr)) {
                return;
            }

            int needInterpolation = 0;
            for (PsiExpression operand : polyExpr.getOperands()) {
                String text = Interp4jPsiUtil.getStringLiteralText(operand);

                // if any part in whole expression is not string literal,
                // the whole expression is wrong.
                if (Objects.isNull(text)) {
                    wrongExpressionType(polyExpr);
                    return;
                }

                Optional<E> parseResult = parser.apply(text);

                if (parseResult.isEmpty()) {
                    // Expression can not be parsed,
                    // it means user typed some expression but in wrong format.
                    // count such expression as interpolatable.
                    needInterpolation++;

                    unpassableExpression(operand);
                    continue;
                }

                E expr = parseResult.get();

                validate((PsiLiteralExpression) operand, expr);

                if (needInterpolationPredicate.apply(expr)) {
                    needInterpolation++;
                }
            }

            // no one part contains expressions for interpolation
            if (needInterpolation == 0) {
                noNeedInterpolation(methodCall);
            }
        }

        private boolean isPlusPolyExpression(PsiPolyadicExpression polyExpr) {
            IElementType token = polyExpr.getOperationTokenType();
            return "PLUS".equals(token.toString());
        }

        private Consumer<E> inspectParsed(PsiMethodCallExpression methodCall, PsiLiteralExpression literal) {
            return expr -> {
                if (!needInterpolationPredicate.apply(expr)) {
                    noNeedInterpolation(methodCall);
                    return;
                }
                validate(literal, expr);
            };
        }

        private void noNeedInterpolation(PsiMethodCallExpression methodCall) {
            holder.registerProblem(
                    methodCall,
                    Interp4jBundle.getMessage("inspection.interpolated.string.no.need.interpolation"),
                    ProblemHighlightType.WARNING,
                    new RemoteMethodCallLocalQuickFix()
            );
        }

        private void unpassableExpression(PsiExpression expression) {
            holder.registerProblem(
                    expression,
                    Interp4jBundle.getMessage("inspection.interpolated.string.expression.can.not.be.parsed"),
                    ProblemHighlightType.GENERIC_ERROR,
                    new RemoteMethodCallLocalQuickFix()
            );
        }

        private void wrongExpressionType(PsiExpression expression) {
            holder.registerProblem(
                    expression,
                    Interp4jBundle.getMessage("inspection.interpolated.string.wrong.expression.type"),
                    ProblemHighlightType.GENERIC_ERROR
            );
        }
    }

    private static class RemoteMethodCallLocalQuickFix implements LocalQuickFix {

        @Override
        public String getFamilyName() {
            return Interp4jBundle.getMessage("inspection.interpolated.string.no.need.interpolation.remove.method.call");
        }

        @Override
        public void applyFix(Project project, ProblemDescriptor descriptor) {
            PsiMethodCallExpression methodCall = (PsiMethodCallExpression) descriptor.getPsiElement();

            PsiFile file = methodCall.getContainingFile();

            WriteCommandAction.runWriteCommandAction(project, null, null, () -> {
                PsiExpressionList arguments = methodCall.getArgumentList();
                methodCall.replace(arguments.getExpressions()[0]);
                JavaCodeStyleManager.getInstance(project).optimizeImports(file);
                UndoUtil.markPsiFileForUndo(file);
            }, file);
        }

        @Override
        public boolean startInWriteAction() {
            return false;
        }
    }

}
