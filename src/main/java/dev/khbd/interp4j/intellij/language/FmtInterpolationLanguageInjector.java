package dev.khbd.interp4j.intellij.language;

import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.psi.PsiLiteralExpression;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import dev.khbd.interp4j.intellij.common.grammar.s.SCode;
import dev.khbd.interp4j.intellij.common.grammar.s.SExpression;
import dev.khbd.interp4j.intellij.common.grammar.s.SExpressionParser;
import dev.khbd.interp4j.intellij.common.grammar.s.SExpressionVisitor;

import java.util.function.Consumer;

/**
 * @author Sergei_Khadanovich
 */
public class FmtInterpolationLanguageInjector extends AbstractInterpolationLanguageInjector {

    FmtInterpolationLanguageInjector() {
        super(Interp4jPsiUtil::isFmtMethodCall);
    }

    @Override
    protected void injectLanguage(MultiHostRegistrar registrar,
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
        return sExpr -> sExpr.visit(new Injector(registrar, context));
    }

    private static class Injector extends InjectionVisitor implements SExpressionVisitor {

        Injector(MultiHostRegistrar registrar, PsiLiteralExpression context) {
            super(registrar, context);
        }

        @Override
        public void visitCode(SCode code) {
            inject(code.start(), code.end());
        }
    }
}
