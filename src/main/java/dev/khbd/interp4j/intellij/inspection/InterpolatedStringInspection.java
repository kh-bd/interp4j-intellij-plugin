package dev.khbd.interp4j.intellij.inspection;

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
import dev.khbd.interp4j.intellij.common.grammar.s.SExpression;
import dev.khbd.interp4j.intellij.common.grammar.s.SExpressionParser;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;
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

            inspectExpression(methodCall, arguments.getExpressions()[0]);
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

                Optional<SExpression> parseResult = SExpressionParser.getInstance().parse(text);

                if (parseResult.isEmpty()) {
                    // Expression can not be parsed,
                    // it means user typed some expression but in wrong format.
                    // count such expression as interpolatable.
                    needInterpolation++;

                    unpassableExpression(operand);
                    continue;
                }

                SExpression sExpr = parseResult.get();
                if (sExpr.hasAnyCode()) {
                    needInterpolation++;
                }
            }

            // no one part contains expressions for interpolation
            if (needInterpolation == 0) {
                interpolationWithoutCodeBlocks(methodCall);
            }
        }

        private boolean isPlusPolyExpression(PsiPolyadicExpression polyExpr) {
            IElementType token = polyExpr.getOperationTokenType();
            return "PLUS".equals(token.toString());
        }

        private void inspectLiteralExpression(PsiMethodCallExpression methodCall,
                                              PsiLiteralExpression literalExpr) {
            String value = Interp4jPsiUtil.getStringLiteralText(literalExpr);
            if (Objects.isNull(value)) {
                wrongExpressionType(literalExpr);
                return;
            }
            SExpressionParser.getInstance().parse(value)
                    .ifPresentOrElse(inspectParsed(methodCall), () -> unpassableExpression(literalExpr));
        }

        private Consumer<SExpression> inspectParsed(PsiMethodCallExpression methodCall) {
            return sExpr -> {
                if (!sExpr.hasAnyCode()) {
                    interpolationWithoutCodeBlocks(methodCall);
                }
            };
        }

        private void interpolationWithoutCodeBlocks(PsiMethodCallExpression methodCall) {
            holder.registerProblem(
                    methodCall,
                    Interp4jBundle.getMessage("inspection.interpolated.string.no.one.expression.found"),
                    ProblemHighlightType.WARNING,
                    new RemoteSMethodCallLocalQuickFix()
            );
        }

        private void unpassableExpression(PsiExpression expression) {
            holder.registerProblem(
                    expression,
                    Interp4jBundle.getMessage("inspection.interpolated.string.expression.can.not.be.parsed"),
                    ProblemHighlightType.GENERIC_ERROR,
                    new RemoteSMethodCallLocalQuickFix()
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

    private static class RemoteSMethodCallLocalQuickFix implements LocalQuickFix {

        @Override
        public String getFamilyName() {
            return Interp4jBundle.getMessage("inspection.interpolated.string.no.one.expression.found.remove.s.call");
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
    }

}
