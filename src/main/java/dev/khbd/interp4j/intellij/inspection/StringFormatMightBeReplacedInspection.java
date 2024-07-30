package dev.khbd.interp4j.intellij.inspection;

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
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiPolyadicExpression;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import dev.khbd.interp4j.intellij.Interp4jBundle;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import dev.khbd.interp4j.intellij.common.StringUtils;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatExpression;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatExpressionParser;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatExpressionVisitor;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatSpecifier;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatText;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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
            if (!Interp4jPsiUtil.isInterpolationEnabled(element)
                || !(element instanceof PsiMethodCallExpression methodCall)
                || !Interp4jPsiUtil.isStringFormatCall(methodCall)) {
                return;
            }

            PsiExpressionList arguments = methodCall.getArgumentList();
            if (arguments.getExpressionCount() < 2) {
                // there should be at least two arguments, but user might not complete typing yet
                return;
            }

            String formatStr = getStringLiteralText(arguments.getExpressions()[0]);

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

            if (parsedExpression.specifiersCount() != arguments.getExpressionCount() - 1) {
                // specifiers count is not the same as arguments count.
                // no way to replace with interpolation
                return;
            }

            ExpressionSimplicityChecker simplicityChecker = new ExpressionSimplicityChecker();
            parsedExpression.visit(simplicityChecker);

            if (simplicityChecker.isSimpleEnough()) {
                holder.registerProblem(
                        methodCall,
                        Interp4jBundle.getMessage("inspection.string.format.usage.might.be.replaced.by.interpolation.s"),
                        ProblemHighlightType.WEAK_WARNING,
                        new ReplaceOnSInterpolationLocalQuickFix(parsedExpression)
                );
            }

            if (!simplicityChecker.hasIndexedSpecifier() && Interp4jPsiUtil.isFmtFunctionAvailable(methodCall)) {
                holder.registerProblem(
                        methodCall,
                        Interp4jBundle.getMessage("inspection.string.format.usage.might.be.replaced.by.interpolation.fmt"),
                        ProblemHighlightType.WEAK_WARNING,
                        new ReplaceOnFmtInterpolationLocalQuickFix(parsedExpression)
                );
            }
        }

        private String getStringLiteralText(PsiExpression expression) {
            if (expression instanceof PsiPolyadicExpression polyExpr) {
                return isCorrectExpressionType(polyExpr) ? expression.getText() : null;
            }

            return Interp4jPsiUtil.getStringLiteralText(expression);
        }

        private boolean isCorrectExpressionType(PsiPolyadicExpression polyExpr) {
            for (PsiExpression operand : polyExpr.getOperands()) {
                String text = Interp4jPsiUtil.getStringLiteralText(operand);

                // if any part in whole expression is not string literal, the whole expression is wrong.
                if (Objects.isNull(text)) {
                    return false;
                }
            }

            return true;
        }

        private static final class ExpressionSimplicityChecker implements FormatExpressionVisitor {

            private static final List<String> ALLOWED_CONVERSIONS = List.of(
                    "s", "S", "d"
            );

            private boolean simple = true;
            private boolean hasIndexedSpecifier = false;

            @Override
            public void visitSpecifier(FormatSpecifier specifier) {
                if (specifier.index() != null
                    || specifier.flags() != null
                    || specifier.width() != null
                    || specifier.precision() != null
                    || !ALLOWED_CONVERSIONS.contains(specifier.conversion().symbols())) {
                    simple = false;
                }
                if (specifier.index() != null) {
                    hasIndexedSpecifier = true;
                }
            }

            boolean isSimpleEnough() {
                return simple;
            }

            boolean hasIndexedSpecifier() {
                return hasIndexedSpecifier;
            }
        }

        /**
         * Quick fix to replace on `fmt` method call.
         */
        private static final class ReplaceOnFmtInterpolationLocalQuickFix extends AbstractReplaceOnStringInterpolationLocalQuickFix {

            ReplaceOnFmtInterpolationLocalQuickFix(FormatExpression expression) {
                super(expression);
            }

            @Override
            public String getFamilyName() {
                return Interp4jBundle.getMessage("inspection.string.format.usage.replace.fmt");
            }

            @Override
            protected InterpolationMethodCallBuildingStrategy createMethodCallBuildingStrategy(PsiExpressionList arguments) {
                return new InterpolationMethodCallBuildingStrategyImpl(arguments, "fmt", FormatSpecifier::toString);
            }

            @Override
            protected void addImport(Project project, PsiJavaFile file) {
                Interp4jPsiUtil.addImport(project, file, "fmt");
            }
        }

        /**
         * Quick fix to replace on `s` method call.
         */
        private static final class ReplaceOnSInterpolationLocalQuickFix extends AbstractReplaceOnStringInterpolationLocalQuickFix {

            ReplaceOnSInterpolationLocalQuickFix(FormatExpression expression) {
                super(expression);
            }

            @Override
            public String getFamilyName() {
                return Interp4jBundle.getMessage("inspection.string.format.usage.replace.s");
            }

            @Override
            protected InterpolationMethodCallBuildingStrategy createMethodCallBuildingStrategy(PsiExpressionList arguments) {
                return new InterpolationMethodCallBuildingStrategyImpl(arguments, "s", __ -> "");
            }

            @Override
            protected void addImport(Project project, PsiJavaFile file) {
                Interp4jPsiUtil.addImport(project, file, "s");
            }
        }

        @RequiredArgsConstructor
        private abstract static class AbstractReplaceOnStringInterpolationLocalQuickFix implements LocalQuickFix {

            private final FormatExpression expression;

            @Override
            public boolean startInWriteAction() {
                return false;
            }

            @Override
            public void applyFix(Project project, ProblemDescriptor descriptor) {
                PsiMethodCallExpression methodCall = (PsiMethodCallExpression) descriptor.getPsiElement();
                PsiJavaFile file = (PsiJavaFile) methodCall.getContainingFile();

                InterpolationMethodCallBuildingStrategy builder = createMethodCallBuildingStrategy(methodCall.getArgumentList());
                expression.visit(builder);

                PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
                PsiExpression interpolationCall = factory.createExpressionFromText(builder.getMethodCallAsString(), methodCall.getContext());

                WriteCommandAction.runWriteCommandAction(project, null, null, () -> {
                    methodCall.replace(interpolationCall);

                    addImport(project, file);
                    JavaCodeStyleManager.getInstance(project).optimizeImports(file);

                    UndoUtil.markPsiFileForUndo(file);
                }, file);
            }

            protected abstract InterpolationMethodCallBuildingStrategy createMethodCallBuildingStrategy(PsiExpressionList arguments);

            /**
             * Add interpolation function import statement.
             */
            protected abstract void addImport(Project project, PsiJavaFile file);
        }

        interface InterpolationMethodCallBuildingStrategy extends FormatExpressionVisitor {

            /**
             * Return built interpolation method call as simple string.
             */
            String getMethodCallAsString();
        }

        @RequiredArgsConstructor
        private static class InterpolationMethodCallBuildingStrategyImpl implements InterpolationMethodCallBuildingStrategy {

            private final PsiExpressionList arguments;
            private final String methodName;
            private final Function<FormatSpecifier, String> specifierFormatter;

            private final StringBuilder builder = new StringBuilder();
            private int seenSpecifiersCount = 0;

            @Override
            public void start() {
                builder.append(methodName);
                builder.append("(");
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

                builder.append(specifierFormatter.apply(specifier));
                builder.append("${");
                builder.append(StringUtils.escapeDoubleQuotes(expression.getText()));
                builder.append("}");

                seenSpecifiersCount += 1;
            }

            @Override
            public void finish() {
                builder.append(")");
            }

            @Override
            public String getMethodCallAsString() {
                return builder.toString();
            }
        }
    }
}
