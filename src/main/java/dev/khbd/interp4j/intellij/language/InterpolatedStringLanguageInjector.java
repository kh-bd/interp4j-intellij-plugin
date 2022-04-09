package dev.khbd.interp4j.intellij.language;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralExpression;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import dev.khbd.interp4j.processor.s.expr.ExpressionPart;
import dev.khbd.interp4j.processor.s.expr.SExpression;
import dev.khbd.interp4j.processor.s.expr.SExpressionParser;
import dev.khbd.interp4j.processor.s.expr.SExpressionVisitor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolatedStringLanguageInjector implements MultiHostInjector {

    private static final String PREFIX = "public class Main { public static Object __target__ = ";
    private static final String SUFFIX = "; }";

    @Override
    public void getLanguagesToInject(MultiHostRegistrar registrar, PsiElement context) {
        PsiLiteralExpression literalExpr = (PsiLiteralExpression) context;

        String value = Interp4jPsiUtil.getStringLiteralText(literalExpr);
        if (Objects.isNull(value)) {
            return;
        }

        if (!Interp4jPsiUtil.insideSMethodCall(literalExpr)) {
            return;
        }

        injectLanguage(registrar, literalExpr, value);
    }

    private void injectLanguage(MultiHostRegistrar registrar,
                                PsiLiteralExpression context,
                                String value) {

        // if expression can not be parsed correctly, it is nothing special.
        // user has to fix compile-time errors.
        // We will inject language later, when user will fix all problems
        SExpressionParser.getInstance().parse(value)
                .ifPresent(inject(registrar, context));
    }

    private Consumer<SExpression> inject(MultiHostRegistrar registrar,
                                         PsiLiteralExpression context) {
        return sExpr -> sExpr.visit(new InjectionVisitor(registrar, context));
    }

    @RequiredArgsConstructor
    private static class InjectionVisitor implements SExpressionVisitor {
        final MultiHostRegistrar registrar;
        final PsiLiteralExpression context;

        @Override
        public void visitExpressionPart(ExpressionPart expressionPart) {
            TextRange range = new TextRange(expressionPart.getStart(), expressionPart.getEnd());
            registrar.startInjecting(JavaLanguage.INSTANCE);
            registrar.addPlace(PREFIX, SUFFIX, (PsiLanguageInjectionHost) context, range);
            registrar.doneInjecting();
        }
    }

    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(PsiLiteralExpression.class);
    }
}
