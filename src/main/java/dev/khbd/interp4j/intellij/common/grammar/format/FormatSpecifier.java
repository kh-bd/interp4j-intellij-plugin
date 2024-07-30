package dev.khbd.interp4j.intellij.common.grammar.format;

import java.util.Objects;

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

    FormatSpecifier(Index index, Conversion conversion) {
        this(index, null, null, null, conversion);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("%");
        if (Objects.nonNull(index)) {
            builder.append(index);
        }
        if (Objects.nonNull(flags)) {
            builder.append(flags);
        }
        if (Objects.nonNull(width)) {
            builder.append(width);
        }
        if (Objects.nonNull(precision)) {
            builder.append(".").append(precision);
        }
        builder.append(conversion.symbols());
        return builder.toString();
    }
}
