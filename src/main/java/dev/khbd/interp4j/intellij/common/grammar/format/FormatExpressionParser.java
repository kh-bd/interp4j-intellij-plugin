/*
 * VTB Group. Do not reproduce without permission in writing.
 * Copyright (c) 2023 VTB Group. All rights reserved.
 */

package dev.khbd.interp4j.intellij.common.grammar.format;

import org.petitparser.context.Result;
import org.petitparser.tools.GrammarParser;

import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
public class FormatExpressionParser {

    private static final FormatExpressionParser INSTANCE = new FormatExpressionParser();

    private final GrammarParser parser = new GrammarParser(new FormatGrammarDefinition());

    private FormatExpressionParser() {
    }

    /**
     * Parse string into format expression.
     *
     * @param formatStr format string
     * @return parsed expression or empty
     */
    public Optional<FormatExpression> parse(String formatStr) {
        Result result = parser.parse(formatStr);

        if (result.isFailure()) {
            return Optional.empty();
        }

        return Optional.of(result.get());
    }

    /**
     * Get parser instance.
     */
    public static FormatExpressionParser getInstance() {
        return INSTANCE;
    }
}
