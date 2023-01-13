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
            FormatText text = (FormatText) args.get(0);
            if (!text.isEmpty()) {
                expression.addPart(text);
            }

            if (args.size() > 1) {
                List<SpecifierAndText> other = (List<SpecifierAndText>) args.get(1);
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
        action("specifierAndText", (List<Object> args) ->
                new SpecifierAndText((FormatSpecifier) args.get(0), (FormatText) args.get(1)));

        // text
        def("text", noneOf("%").star());
        action("text", (List<Object> args) -> {
            String text = args.stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining(""));
            return new FormatText(text);
        });


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
                new FormatSpecifier((Index) args.get(1),
                        (String) args.get(2),
                        (Integer) args.get(3), (Integer) args.get(4),
                        (Conversion) args.get(5)
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
                .map(it -> {
                    List<Object> args = (List<Object>) it;
                    int position = Integer.parseInt((String) args.get(0) + (String) args.get(1));
                    return new NumericIndex(position);
                });
    }

    private static Parser number() {
        return digit().map(asString())
                .seq(digit().star().flatten())
                .map(it -> {
                    List<Object> args = (List<Object>) it;
                    return Integer.parseInt((String) args.get(0) + (String) args.get(1));
                });
    }

    private static Parser implicitIndex() {
        return character('<').map(it -> new ImplicitIndex());
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
                .or(character('t').seq(dateTimeConversionSuffix)
                        .map((List<Object> args) -> Character.toString((Character) args.get(0)) + args.get(1)))
                .or(character('T').seq(dateTimeConversionSuffix)
                        .map((List<Object> args) -> Character.toString((Character) args.get(0)) + args.get(1)))
                .or(character('%'))
                .or(character('n'))
                .map(asString()).map(conv -> new Conversion((String) conv));
    }

    private static Parser dateTimeConversionSuffix() {
        return anyOf("HIklMSLNPzZsQBbHAaCYyjmdeRTrDFc");
    }

    private static Parser flags() {
        return anyOf("-#+ 0,(").plus()
                .map((List<Object> args) -> args.stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining("")));
    }

    private static Parser character(char c) {
        return CharacterParser.of(c);
    }

    private static <T> Function<T, String> asString() {
        return Objects::toString;
    }

    private record SpecifierAndText(FormatSpecifier specifier, FormatText text) {
    }
}
