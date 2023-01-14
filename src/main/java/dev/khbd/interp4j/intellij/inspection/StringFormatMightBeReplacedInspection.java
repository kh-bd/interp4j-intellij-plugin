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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStaticStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import dev.khbd.interp4j.core.Interpolations;
import dev.khbd.interp4j.intellij.Interp4jBundle;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatExpression;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatExpressionParser;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatExpressionVisitor;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatSpecifier;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatText;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author Sergei_Khadanovich
 */
public class StringFormatMightBeReplacedInspection extends LocalInspectionTool {

    @Override
    public PsiElementVisitor buildVisitor(ProblemsHolder holder, boolean isOnTheFly) {
        return new StringFormatInvocationVisitor(holder);
    }

    @RequiredArgsConstructor
    static class StringFormatInvocationVisitor extends PsiElementVisitor {

        private final ProblemsHolder holder;

        @Override
        public void visitElement(@NotNull PsiElement element) {
            if (!(element instanceof PsiMethodCallExpression)) {
                return;
            }

            PsiMethodCallExpression methodCall = (PsiMethodCallExpression) element;
            if (!Interp4jPsiUtil.isStringFormatCall(methodCall)) {
                return;
            }

            PsiExpressionList arguments = methodCall.getArgumentList();
            if (arguments.isEmpty()) {
                // there should be at least two arguments, but user might not complete typing yet
                return;
            }

            String formatStr = Interp4jPsiUtil.getStringLiteralText(arguments.getExpressions()[0]);

            // user can use static field to defined template
            // such cases are not detected yet, but it can be implemented later
            if (Objects.isNull(formatStr)) {
                return;
            }

            FormatExpression parsedExpression = FormatExpressionParser.getInstance().parse(formatStr).orElse(null);
            if (Objects.isNull(parsedExpression)) {
                // user typed in something, but it does not look like correct format template
                return;
            }

            if (!isSimpleEnough(parsedExpression)
                    || parsedExpression.specifiersCount() != arguments.getExpressionCount() - 1) {
                // expression is too complicated to rewrite it or
                // arguments count is wrong
                return;
            }

            holder.registerProblem(
                    methodCall,
                    Interp4jBundle.getMessage("inspection.string.format.usage.might.be.replaced.by.interpolation"),
                    ProblemHighlightType.WEAK_WARNING,
                    new ReplaceOnStringInterpolationLocalQuickFix(parsedExpression)
            );
        }

        private boolean isSimpleEnough(FormatExpression expression) {
            ExpressionSimplicityChecker checker = new ExpressionSimplicityChecker();
            expression.visit(checker);
            return checker.isSimpleEnough();
        }

        private static final class ExpressionSimplicityChecker implements FormatExpressionVisitor {

            private static final List<String> ALLOWED_CONVERSIONS = List.of(
                    "s", "S", "d"
            );

            private boolean simple = true;

            @Override
            public void visitSpecifier(FormatSpecifier specifier) {
                if (specifier.index() != null
                        || specifier.flags() != null
                        || specifier.width() != null
                        || specifier.precision() != null
                        || !ALLOWED_CONVERSIONS.contains(specifier.conversion().symbols())) {
                    simple = false;
                }
            }

            boolean isSimpleEnough() {
                return simple;
            }
        }

        @RequiredArgsConstructor
        private static class ReplaceOnStringInterpolationLocalQuickFix implements LocalQuickFix {

            private final FormatExpression expression;

            @Override
            public String getFamilyName() {
                return Interp4jBundle.getMessage("inspection.string.format.usage.replace");
            }

            @Override
            public IntentionPreviewInfo generatePreview(Project project, ProblemDescriptor previewDescriptor) {
                return IntentionPreviewInfo.EMPTY;
            }

            @Override
            public void applyFix(Project project, ProblemDescriptor descriptor) {
                PsiMethodCallExpression methodCall = (PsiMethodCallExpression) descriptor.getPsiElement();
                PsiJavaFile file = (PsiJavaFile) methodCall.getContainingFile();

                SMethodCallBuilder builder = new SMethodCallBuilder(methodCall.getArgumentList());
                expression.visit(builder);

                PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
                PsiExpression sMethodCall = factory.createExpressionFromText(builder.getSMethodCallAsString(), methodCall.getContext());

                WriteCommandAction.runWriteCommandAction(project, null, null, () -> {
                    methodCall.replace(sMethodCall);

                    addSImport(project, file, factory);
                    JavaCodeStyleManager.getInstance(project).optimizeImports(file);

                    UndoUtil.markPsiFileForUndo(file);
                }, file);
            }

            private void addSImport(Project project, PsiJavaFile file, PsiElementFactory factory) {
                PsiClass interpolationsClass = JavaPsiFacade.getInstance(project)
                        .findClass(Interpolations.class.getCanonicalName(), GlobalSearchScope.allScope(project));

                if (Objects.isNull(interpolationsClass)) {
                    return;
                }

                PsiImportStaticStatement sImport = factory.createImportStaticStatement(interpolationsClass, "s");

                PsiImportList imports = file.getImportList();
                // imports can not be null, because user is modifying source file, not compiled file
                imports.add(sImport);
            }
        }

        @RequiredArgsConstructor
        private static class SMethodCallBuilder implements FormatExpressionVisitor {

            private final PsiExpressionList arguments;

            private final StringBuilder builder = new StringBuilder();

            private int seenSpecifiersCount = 0;

            @Override
            public void start() {
                builder.append("s(");
            }

            @Override
            public void visitText(FormatText text) {
                builder.append(text.text());
            }

            @Override
            public void visitSpecifier(FormatSpecifier specifier) {
                // there is a format string at 0 position, so we need to ignore it
                int index = seenSpecifiersCount + 1;

                PsiExpression expression = arguments.getExpressions()[index];

                if (expression instanceof PsiReferenceExpression) {
                    builder.append("$");
                    builder.append(expression.getText());
                } else {
                    builder.append("${");
                    builder.append(expression.getText());
                    builder.append("}");
                }

                seenSpecifiersCount += 1;
            }

            @Override
            public void finish() {
                builder.append(")");
            }

            String getSMethodCallAsString() {
                return builder.toString();
            }
        }
    }
}
