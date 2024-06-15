package dev.khbd.interp4j.intellij.common.grammar.fmt;

import lombok.NonNull;
import org.petitparser.context.Result;
import org.petitparser.tools.GrammarParser;

import java.util.Optional;

/**
 * 'fmt' expression parser.
 *
 * @author Sergei_Khadanovich
 */
public final class FmtExpressionParser {

    private static final FmtExpressionParser INSTANCE = new FmtExpressionParser();

    private FmtExpressionParser() {
    }

    private final GrammarParser parser = new GrammarParser(new FmtGrammarDefinition());

    /**
     * Parse supplied string literal to {@link FmtExpression}.
     *
     * @param literal string literal
     * @return parsed {@link FmtExpression} or empty if parsing was failed
     */
    public Optional<FmtExpression> parse(@NonNull String literal) {
        Result result = parser.parse(literal);
        if (result.isFailure()) {
            return Optional.empty();
        }
        return Optional.of(result.get());
    }

    public static FmtExpressionParser getInstance() {
        return INSTANCE;
    }
}
