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

    FormatSpecifier(Index index, Conversion conversion) {
        this(index, null, null, null, conversion);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("%");
        if (index != null) {
            builder.append(index);
        }
        if (flags != null) {
            builder.append(flags);
        }
        if (width != null) {
            builder.append(width);
        }
        if (precision != null) {
            builder.append(".").append(precision);
        }
        builder.append(conversion.symbols());
        return builder.toString();
    }
}
