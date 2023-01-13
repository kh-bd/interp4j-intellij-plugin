package dev.khbd.interp4j.intellij.common.grammar.format;

/**
 * Format specifier in format expression.
 *
 * <p>Note: Only conversion is required by grammar. Any other part can be absent.
 *
 * @author Sergei_Khadanovich
 */
public record FormatSpecifier(Index index, String flags, Integer width, Integer precision,
                              Conversion conversion) implements FormatExpressionPart {

    FormatSpecifier(Conversion conversion) {
        this(null, null, null, null, conversion);
    }
}
