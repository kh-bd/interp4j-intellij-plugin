package dev.khbd.interp4j.intellij.language;

import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.psi.PsiLiteralExpression;
import dev.khbd.interp4j.intellij.common.Interp4jPsiUtil;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtCode;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtExpression;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtExpressionParser;
import dev.khbd.interp4j.intellij.common.grammar.fmt.FmtExpressionVisitor;
import dev.khbd.interp4j.intellij.common.grammar.fmt.Position;

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
        FmtExpressionParser.getInstance().parse(value)
                .ifPresent(inject(registrar, context));
    }

    private Consumer<FmtExpression> inject(MultiHostRegistrar registrar,
                                           PsiLiteralExpression context) {
        return sExpr -> sExpr.visit(new Injector(registrar, context));
    }

    private static class Injector extends InjectionVisitor implements FmtExpressionVisitor {

        Injector(MultiHostRegistrar registrar, PsiLiteralExpression context) {
            super(registrar, context);
        }

        @Override
        public void visitCodePart(FmtCode code) {
            Position position = code.getPosition();
            inject(position.getStart(), position.getEnd());
        }
    }
}
