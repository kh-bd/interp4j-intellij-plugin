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
import dev.khbd.interp4j.intellij.common.grammar.format.Conversion;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatExpression;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatExpressionParser;
import dev.khbd.interp4j.intellij.common.grammar.format.FormatExpressionVisitor;
import dev.khbd.interp4j.intellij.common.grammar.format.Index;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

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

            private boolean simple = true;

            @Override
            public void visitIndex(Index index) {
                if (Objects.nonNull(index)) {
                    simple = false;
                }
            }

            @Override
            public void visitFlags(String flag) {
                if (Objects.nonNull(flag)) {
                    simple = false;
                }
            }

            @Override
            public void visitWidth(Integer width) {
                if (Objects.nonNull(width)) {
                    simple = false;
                }
            }

            @Override
            public void visitPrecision(Integer precision) {
                if (Objects.nonNull(precision)) {
                    simple = false;
                }
            }

            @Override
            public void visitConversion(Conversion conversion) {
                String symbols = conversion.symbols();
                if (!symbols.equals("s") && !symbols.equals("S")) {
                    simple = false;
                }
            }

            boolean isSimpleEnough() {
                return simple;
            }
        }
    }
}
