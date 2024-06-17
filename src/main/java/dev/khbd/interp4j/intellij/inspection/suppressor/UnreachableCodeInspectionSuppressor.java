package dev.khbd.interp4j.intellij.inspection.suppressor;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiStatement;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Sergei Khadanovich
 */
public class UnreachableCodeInspectionSuppressor implements InspectionSuppressor {

    private static final String TOOL_ID = "UnreachableCode";

    @Override
    public boolean isSuppressedFor(@NotNull PsiElement psiElement, @NotNull String toolId) {
        if (!TOOL_ID.equals(toolId)) {
            return false;
        }

        if (psiElement instanceof PsiCodeBlock block && block.getStatementCount() != 0) {
            return containsInterpolation(block.getStatements()[0]);
        } else if (psiElement instanceof PsiStatement statement) {
            PsiElement prevStatement = findPrevStatement(statement);
            if (prevStatement != null) {
                return containsInterpolation(prevStatement);
            }
        }

        return false;
    }

    private PsiElement findPrevStatement(PsiStatement statement) {
        for (PsiElement prev = statement.getPrevSibling(); prev != null; prev = prev.getPrevSibling()) {
            if (prev instanceof PsiStatement) {
                return prev;
            }
        }
        return null;
    }

    private boolean containsInterpolation(PsiElement element) {
        FormattersCallDetector detector = new FormattersCallDetector();
        element.accept(detector);
        return detector.found;
    }

    @Override
    public SuppressQuickFix @NotNull [] getSuppressActions(@Nullable PsiElement psiElement, @NotNull String toolId) {
        return SuppressQuickFix.EMPTY_ARRAY;
    }

    static final class FormattersCallDetector extends JavaRecursiveElementVisitor {

        boolean found = false;

        @Override
        public void visitMethodCallExpression(@NotNull PsiMethodCallExpression call) {
            if (Interp4jPsiUtil.isSMethodCall(call) || Interp4jPsiUtil.isFmtMethodCall(call)) {
                this.found = true;
            }
            super.visitMethodCallExpression(call);
        }
    }
}
