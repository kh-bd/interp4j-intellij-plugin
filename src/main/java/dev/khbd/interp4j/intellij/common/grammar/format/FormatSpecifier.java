package dev.khbd.interp4j.intellij.common.grammar.format;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Format specifier in format expression.
 *
 * <p>Note: Only conversion is required by grammar. Any other part can be absent.
 *
 * @author Sergei_Khadanovich
 */
@Value
@AllArgsConstructor
public class FormatSpecifier implements FormatExpressionPart {

    Index index;
    String flags;
    Integer width;
    Integer precision;
    Conversion conversion;

    FormatSpecifier(Conversion conversion) {
        this(null, null, null, null, conversion);
    }

    FormatSpecifier(Index index, Conversion conversion) {
        this(index, null, null, null, conversion);
    }
}
