package dev.khbd.interp4j.intellij.common.grammar.format;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
public class FormatGrammarDefinitionTest {

    private final FormatExpressionParser parser = FormatExpressionParser.getInstance();

    @Test
    public void parse_theSimplestString_parse() {
        Optional<FormatExpression> result = parser.parse("%s");

        assertThat(result).hasValue(
                new FormatExpression()
                        .addPart(new FormatSpecifier(new Conversion("s")))
        );
    }

    @Test
    public void parse_explicitArgumentIndexes_parse() {
        Optional<FormatExpression> result = parser.parse("%4$2s %3$2s %2$2s %1$2s");

        assertThat(result).hasValue(
                new FormatExpression()
                        .addPart(new FormatSpecifier(new NumericIndex(4), null, 2, null, new Conversion("s")))
                        .addPart(new FormatText(" "))
                        .addPart(new FormatSpecifier(new NumericIndex(3), null, 2, null, new Conversion("s")))
                        .addPart(new FormatText(" "))
                        .addPart(new FormatSpecifier(new NumericIndex(2), null, 2, null, new Conversion("s")))
                        .addPart(new FormatText(" "))
                        .addPart(new FormatSpecifier(new NumericIndex(1), null, 2, null, new Conversion("s")))
        );
    }

    @Test
    public void pare_floatingWithPrecision_parse() {
        Optional<FormatExpression> result = parser.parse("e = %+10.4f");

        assertThat(result).hasValue(
                new FormatExpression()
                        .addPart(new FormatText("e = "))
                        .addPart(new FormatSpecifier(null, "+", 10, 4, new Conversion("f")))
        );
    }

    @Test
    public void pare_floatingWithFlags_parse() {
        Optional<FormatExpression> result = parser.parse("Amount gained or lost since last statement: $ %(,.2f");

        assertThat(result).hasValue(
                new FormatExpression()
                        .addPart(new FormatText("Amount gained or lost since last statement: $ "))
                        .addPart(new FormatSpecifier(null, "(,", null, 2, new Conversion("f")))
        );
    }
}