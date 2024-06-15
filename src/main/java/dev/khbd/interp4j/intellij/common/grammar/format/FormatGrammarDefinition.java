package dev.khbd.interp4j.intellij.common.grammar.format;

import static org.petitparser.parser.primitive.CharacterParser.anyOf;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.noneOf;

import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.tools.GrammarDefinition;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Sergei_Khadanovich
 */
class FormatGrammarDefinition extends GrammarDefinition {

    FormatGrammarDefinition() {
        def("start",
                ref("text")
                        .seq(ref("specifierAndText").star())
                        .end()
        );
        action("start", (List<Object> args) -> {
            FormatExpression expression = new FormatExpression();
            FormatText text = getTyped(args, 0);
            if (!text.isEmpty()) {
                expression.addPart(text);
            }

            if (args.size() > 1) {
                List<SpecifierAndText> other = getTyped(args, 1);
                for (SpecifierAndText specifierAndText : other) {
                    expression.addPart(specifierAndText.specifier);
                    if (!specifierAndText.text.isEmpty()) {
                        expression.addPart(specifierAndText.text);
                    }
                }
            }

            return expression;
        });

        // text and specifier
        def("specifierAndText", ref("specifier").seq(ref("text")));
        action("specifierAndText", (List<Object> args) -> new SpecifierAndText(getTyped(args, 0), getTyped(args, 1)));

        // text
        def("text", noneOf("%").star());
        action("text", listAsString().andThen(FormatText::new));

        // specifier
        def("specifier",
                character('%')
                        .seq(ref("index").optional())
                        .seq(ref("flags").optional())
                        .seq(ref("width").optional())
                        .seq(ref("precision").optional())
                        .seq(ref("conversion"))
        );
        action("specifier", (List<Object> args) ->
                new FormatSpecifier(getTyped(args, 1), getTyped(args, 2),
                        getTyped(args, 3), getTyped(args, 4),
                        getTyped(args, 5)
                ));

        // index
        def("index", ref("numericIndex").or(ref("implicitIndex")));
        def("numericIndex", numericIndex());
        def("implicitIndex", implicitIndex());

        // flags
        def("flags", flags());

        // conversion
        def("conversion", conversion());

        // width
        def("width", number());

        // precision
        def("precision", character('.').seq(number()).pick(1));
    }

    private static Parser numericIndex() {
        return digit().map(asString())
                .seq(digit().star().flatten())
                .seq(character('$'))
                .map((List<Object> args) -> {
                    String firstDigit = getTyped(args, 0);
                    int position = Integer.parseInt(firstDigit + getTyped(args, 1));
                    return new NumericIndex(position);
                });
    }

    private static Parser number() {
        return digit().map(asString())
                .seq(digit().star().flatten())
                .map((List<Object> args) -> {
                    String firstDigit = getTyped(args, 0);
                    return Integer.parseInt(firstDigit + getTyped(args, 1));
                });
    }

    private static Parser implicitIndex() {
        return character('<').map(it -> ImplicitIndex.INSTANCE);
    }

    private static Parser conversion() {
        Parser dateTimeConversionSuffix = dateTimeConversionSuffix();
        return anyOf("bB")
                .or(anyOf("hH"))
                .or(anyOf("sS"))
                .or(anyOf("cC"))
                .or(character('d'))
                .or(character('o'))
                .or(anyOf("xX"))
                .or(anyOf("eE"))
                .or(character('f'))
                .or(anyOf("gG"))
                .or(anyOf("aA"))
                .or(character('t').seq(dateTimeConversionSuffix).map(listAsString()))
                .or(character('T').seq(dateTimeConversionSuffix).map(listAsString()))
                .or(character('%'))
                .or(character('n'))
                .map(asString()).map(conv -> new Conversion((String) conv));
    }

    private static Parser dateTimeConversionSuffix() {
        return anyOf("HIklMSLNPzZsQBbHAaCYyjmdeRTrDFc");
    }

    private static Parser flags() {
        return anyOf("-#+ 0,(").plus().map(listAsString());
    }

    private static Parser character(char c) {
        return CharacterParser.of(c);
    }

    private static <T> Function<T, String> asString() {
        return Objects::toString;
    }

    private static Function<List<Object>, String> listAsString() {
        return args -> args.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(""));

    }

    private record SpecifierAndText(FormatSpecifier specifier, FormatText text) {
    }

    @SuppressWarnings("unchecked")
    private static <T> T getTyped(List<Object> values, int index) {
        return (T) values.get(index);
    }

}
