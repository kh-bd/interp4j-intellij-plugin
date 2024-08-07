package dev.khbd.interp4j.intellij.common;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStaticStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.search.GlobalSearchScope;
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

    public static final String S = "s";
    public static final String FMT = "fmt";

    /**
     * Check if {@link Interpolations} class is in scope.
     *
     * @param element psi element
     * @return {@literal true} if class is in scope and {@literal false} otherwise
     */
    public static boolean isInterpolationEnabled(PsiElement element) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(element.getProject());

        PsiClass psiClass = JavaPsiFacade.getInstance(element.getProject())
                .findClass(Interpolations.class.getCanonicalName(), scope);

        return Objects.nonNull(psiClass);
    }

    /**
     * Check if {@link Interpolations#fmt} function is in scope.
     *
     * @param element psi element
     * @return {@literal true} if function is in scope and {@literal false} otherwise
     */
    public static boolean isFmtFunctionAvailable(PsiElement element) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(element.getProject());

        PsiClass psiClass = JavaPsiFacade.getInstance(element.getProject())
                .findClass(Interpolations.class.getCanonicalName(), scope);

        if (psiClass == null) {
            return false;
        }

        return psiClass.findMethodsByName(FMT).length != 0;
    }

    /**
     * Check is supplied method call a 's' method call.
     *
     * @param methodCall method call
     * @return {@literal true} if it is a 's' method call and {@literal false} otherwise
     */
    public static boolean isSMethodCall(@NonNull PsiMethodCallExpression methodCall) {
        return isInterpolatorCall(methodCall, S);
    }

    /**
     * Check is supplied method call a 'fmt' method call.
     *
     * @param methodCall method call
     * @return {@literal true} if it is a 'fmt' method call and {@literal false} otherwise
     */
    public static boolean isFmtMethodCall(@NonNull PsiMethodCallExpression methodCall) {
        return isInterpolatorCall(methodCall, FMT);
    }

    private static boolean isInterpolatorCall(@NonNull PsiMethodCallExpression methodCall, @NonNull String methodName) {
        PsiMethod originalMethod = methodCall.resolveMethod();
        return Objects.nonNull(originalMethod)
               && isMethodFromClassWithName(originalMethod, methodName, Interpolations.class, true);
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
               && isMethodFromClassWithName(originalMethod, "interpolate", SInterpolator.class, false);
    }

    /**
     * Check whether method call is {@link String#format(String, Object...)} call.
     *
     * @param methodCall method call
     */
    public static boolean isStringFormatCall(@NonNull PsiMethodCallExpression methodCall) {
        PsiMethod originalMethod = methodCall.resolveMethod();
        if (Objects.isNull(originalMethod)) {
            return false;
        }

        if (!isMethodFromClassWithName(originalMethod, "format", String.class, true)) {
            return false;
        }

        PsiParameterList arguments = originalMethod.getParameterList();

        // String.format(template, args)
        // need to check arguments because String class has additional format method with
        // locale argument at first position
        return arguments.getParametersCount() == 2;
    }

    private boolean isMethodFromClassWithName(PsiMethod method, String name, Class<?> clazz, boolean isStatic) {
        if (!method.getName().equals(name)) {
            return false;
        }
        if (isStatic && !method.getModifierList().hasExplicitModifier(PsiModifier.STATIC)) {
            return false;
        }
        PsiElement parent = method.getParent();
        if (!(parent instanceof PsiClass)) {
            return false;
        }
        PsiClass psiClass = (PsiClass) parent;
        return clazz.getCanonicalName().equals(psiClass.getQualifiedName());
    }

    /**
     * Get string text from psi expression.
     *
     * @param expression psi expression
     * @return string text
     */
    @Nullable
    public static String getStringLiteralText(@NonNull PsiExpression expression) {
        if (!(expression instanceof PsiLiteralExpression literalExpr)) {
            return null;
        }

        Object value = literalExpr.getValue();

        if (Objects.isNull(value)) {
            return null;
        }

        if (!(value instanceof String)) {
            return null;
        }

        // get text instead of value to get original string with all characters
        return literalExpr.getText();
    }

    /**
     * Check if expression is string literal or not.
     *
     * @param expression expression
     * @return {@literal true} if expression is string literal and {@literal false} otherwise
     */
    public static boolean isStringLiteral(@NonNull PsiExpression expression) {
        if (!(expression instanceof PsiLiteralExpression literalExpr)) {
            return false;
        }
        // value can be null, but it's correct because null instanceof String is false
        return literalExpr.getValue() instanceof String;
    }

    /**
     * Add static import for {@link Interpolations#s} function.
     *
     * @param project project
     * @param file    java file
     */
    public static void addSImport(@NonNull Project project, @NonNull PsiJavaFile file) {
        addImport(project, file, S);
    }

    /**
     * Add static import for {@link Interpolations#fmt} function.
     *
     * @param project project
     * @param file    java file
     */
    public static void addFmtImport(@NonNull Project project, @NonNull PsiJavaFile file) {
        addImport(project, file, FMT);
    }

    private static void addImport(@NonNull Project project, @NonNull PsiJavaFile file, @NonNull String methodName) {
        PsiClass interpolationsClass = JavaPsiFacade.getInstance(project)
                .findClass(Interpolations.class.getCanonicalName(), GlobalSearchScope.allScope(project));

        if (Objects.isNull(interpolationsClass)) {
            return;
        }

        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        PsiImportStaticStatement importStatement = factory.createImportStaticStatement(interpolationsClass, methodName);

        PsiImportList imports = file.getImportList();
        // imports can not be null, because user is modifying source file, not compiled file
        imports.add(importStatement);
    }
}
