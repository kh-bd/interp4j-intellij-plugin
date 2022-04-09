package dev.khbd.interp4j.intellij.common;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import dev.khbd.interp4j.core.Interpolations;
import dev.khbd.interp4j.core.internal.s.SInterpolator;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author Sergei_Khadanovich
 */
@UtilityClass
public class Interp4jPsiUtil {

    /**
     * Check is supplied method call a 's' method call.
     *
     * @param methodCall method call
     * @return {@literal true} if it is a 's' method call and {@literal false} otherwise
     */
    public static boolean isSMethodCall(@NonNull PsiMethodCallExpression methodCall) {
        PsiMethod originalMethod = methodCall.resolveMethod();
        return Objects.nonNull(originalMethod)
                && isMethodFromClassWithName(originalMethod, "s", Interpolations.class);
    }

    /**
     * Check is supplied method call a 'interpolate' method call.
     *
     * @param methodCall method call
     * @return {@literal true} if it is a 'interpolate' method call and {@literal false} otherwise
     */
    public static boolean isInterpolateMethodCall(@NonNull PsiMethodCallExpression methodCall) {
        PsiMethod originalMethod = methodCall.resolveMethod();
        return Objects.nonNull(originalMethod)
                && isMethodFromClassWithName(originalMethod, "interpolate", SInterpolator.class);
    }

    /**
     * Get string text from psi expression.
     *
     * @param expression psi expression
     * @return string text
     */
    @Nullable
    public static String getStringLiteralText(@NonNull PsiExpression expression) {
        if (!(expression instanceof PsiLiteralExpression)) {
            return null;
        }

        PsiLiteralExpression literalExpression = (PsiLiteralExpression) expression;

        Object value = literalExpression.getValue();

        if (Objects.isNull(value)) {
            return null;
        }

        if (!(value instanceof String)) {
            return null;
        }

        // get text instead of value to get original string with all characters
        return literalExpression.getText();
    }

    /**
     * Check is supplied expression part of `s` method call.
     *
     * @param literalExpression literal expression
     * @return {@literal true} if supplied expression is actual parameter of `s` method invocation
     * and {@literal false} otherwise
     */
    public static boolean insideSMethodCall(@NonNull PsiLiteralExpression literalExpression) {
        PsiElement mayBeExpressionList = literalExpression.getParent();
        if (!(mayBeExpressionList instanceof PsiExpressionList)) {
            return false;
        }
        PsiExpressionList expressionList = (PsiExpressionList) mayBeExpressionList;
        PsiElement mayBeMethodCall = expressionList.getParent();
        if (!(mayBeMethodCall instanceof PsiMethodCallExpression)) {
            return false;
        }
        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) mayBeMethodCall;
        return Interp4jPsiUtil.isSMethodCall(methodCall);
    }

    private boolean isMethodFromClassWithName(PsiMethod method, String name, Class<?> clazz) {
        if (!method.getName().equals(name)) {
            return false;
        }
        PsiElement parent = method.getParent();
        if (!(parent instanceof PsiClass)) {
            return false;
        }
        PsiClass psiClass = (PsiClass) parent;
        return clazz.getCanonicalName().equals(psiClass.getQualifiedName());
    }
}
