package dev.khbd.interp4j.intellij.inspection;

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.undo.UndoUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiPolyadicExpression;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import dev.khbd.interp4j.intellij.Interp4jBundle;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * @author Sergei_Khadanovich
 */
public class StringConcatMightBeReplacedInspection extends LocalInspectionTool {

    @Override
    @NotNull
    public PsiElementVisitor buildVisitor(ProblemsHolder problems, boolean isOnTheFly) {
        return new StringConcatInvocationVisitor(problems);
    }

    @RequiredArgsConstructor
    static class StringConcatInvocationVisitor extends PsiElementVisitor {
        private final ProblemsHolder problems;

        @Override
        public void visitElement(PsiElement element) {
            if (!(element instanceof PsiPolyadicExpression poly) || !isPlusExpression(poly)) {
                return;
            }

            if (mightBeReplaced(poly.getOperands())) {
                problems.registerProblem(
                        poly,
                        Interp4jBundle.getMessage("inspection.string.concat.usage.might.be.replaced.by.interpolation"),
                        ProblemHighlightType.WEAK_WARNING,
                        new ReplaceStringConcatQuickFix()
                );
            }
        }

        private boolean mightBeReplaced(PsiExpression[] operands) {
            return existsStringLiteral(operands) && existsNonLiteral(operands);
        }

        private boolean existsStringLiteral(PsiExpression[] operands) {
            return Stream.of(operands).anyMatch(Interp4jPsiUtil::isStringLiteral);
        }

        private boolean existsNonLiteral(PsiExpression[] operands) {
            return Stream.of(operands).anyMatch(expr -> !(expr instanceof PsiLiteralExpression));
        }

        private boolean isPlusExpression(PsiPolyadicExpression poly) {
            return "PLUS".equals(poly.getOperationTokenType().toString());
        }

        private static class ReplaceStringConcatQuickFix implements LocalQuickFix {

            @Override
            @NotNull
            public String getFamilyName() {
                return Interp4jBundle.getMessage("inspection.string.concat.usage.replace");
            }

            @Override
            public void applyFix(Project project, ProblemDescriptor problem) {
                PsiPolyadicExpression poly = (PsiPolyadicExpression) problem.getPsiElement();
                PsiJavaFile file = (PsiJavaFile) poly.getContainingFile();

                PsiExpression sMethodCall = buildSMethodCall(project, poly);

                WriteCommandAction.runWriteCommandAction(project, null, null, () -> {
                    poly.replace(sMethodCall);

                    Interp4jPsiUtil.addSImport(project, file);
                    JavaCodeStyleManager.getInstance(project).optimizeImports(file);

                    UndoUtil.markPsiFileForUndo(file);
                }, file);
            }

            private PsiExpression buildSMethodCall(Project project, PsiPolyadicExpression poly) {
                String callAsString = buildSMethodCallAsString(poly);
                return JavaPsiFacade.getInstance(project).getElementFactory()
                        .createExpressionFromText(callAsString, poly.getContext());
            }

            private String buildSMethodCallAsString(PsiPolyadicExpression poly) {
                StringBuilder builder = new StringBuilder("s(\"");
                for (PsiExpression operand : poly.getOperands()) {
                    if (Interp4jPsiUtil.isStringLiteral(operand)) {
                        PsiLiteralExpression literal = (PsiLiteralExpression) operand;
                        builder.append((String) literal.getValue());
                    } else {
                        builder.append("${");
                        builder.append(operand.getText());
                        builder.append("}");
                    }
                }
                builder.append("\")");
                return builder.toString();
            }

            @Override
            @NotNull
            public IntentionPreviewInfo generatePreview(Project project, ProblemDescriptor problem) {
                return IntentionPreviewInfo.EMPTY;
            }
        }

    }

}
