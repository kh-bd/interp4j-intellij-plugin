package dev.khbd.interp4j.intellij.common;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import dev.khbd.interp4j.core.Interpolations;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Objects;

/**
 * @author Sergei_Khadanovich
 */
@UtilityClass
public class Interp4jPsiUtil {

    private static final String S_METHOD_NAME = "s";

    /**
     * Check is supplied method call a 's' method call.
     *
     * @param methodCall method call
     * @return {@literal true} if it is a 's' method call and {@literal false} otherwise
     */
    public static boolean isSMethodCall(@NonNull PsiMethodCallExpression methodCall) {
        PsiMethod originalMethod = methodCall.resolveMethod();
        return Objects.nonNull(originalMethod) && isSMethod(originalMethod);
    }

    private boolean isSMethod(PsiMethod method) {
        if (!method.getName().equals(S_METHOD_NAME)) {
            return false;
        }
        PsiElement parent = method.getParent();
        if (!(parent instanceof PsiClass)) {
            return false;
        }
        PsiClass psiClass = (PsiClass) parent;
        return Interpolations.class.getCanonicalName().equals(psiClass.getQualifiedName());
    }
}
