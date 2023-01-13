package dev.khbd.interp4j.intellij.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiMethodCallExpression;
import dev.khbd.interp4j.intellij.Interp4jBundle;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatExpression;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatExpressionParser;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatExpressionVisitor;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatSpecifier;
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

            if (isSimpleEnough(parsedExpression)) {
                holder.registerProblem(
                        methodCall,
                        Interp4jBundle.getMessage("inspection.string.format.usage.might.be.replaced.by.interpolation"),
                        ProblemHighlightType.WEAK_WARNING,
                        LocalQuickFix.EMPTY_ARRAY
                );
            }

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
    }
}
