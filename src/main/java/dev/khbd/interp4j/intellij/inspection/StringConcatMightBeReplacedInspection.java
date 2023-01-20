package dev.khbd.interp4j.intellij.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiPolyadicExpression;
import dev.khbd.interp4j.intellij.Interp4jBundle;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * @author Sergei_Khadanovich
 */
public class StringConcatMightBeReplacedInspection extends LocalInspectionTool {

    @Override
    public PsiElementVisitor buildVisitor(ProblemsHolder holder, boolean isOnTheFly) {
        return new StringConcatInvocationVisitor(holder);
    }

    @RequiredArgsConstructor
    static class StringConcatInvocationVisitor extends PsiElementVisitor {
        private final ProblemsHolder holder;

        @Override
        public void visitElement(PsiElement element) {
            if (!(element instanceof PsiPolyadicExpression poly) || !isPlusExpression(poly)) {
                return;
            }

            if (mightBeReplaced(poly.getOperands())) {
                holder.registerProblem(
                        poly,
                        Interp4jBundle.getMessage("inspection.string.concat.usage.might.be.replaced.by.interpolation"),
                        ProblemHighlightType.WEAK_WARNING
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
    }

}
