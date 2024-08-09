package dev.khbd.interp4j.intellij.common.grammar.fmt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * @author Sergei Khadanovich
 */
public class FmtExpressionParserTest {

    private static final FmtExpressionParser PARSER = FmtExpressionParser.getInstance();

    @Test
    public void parse_specialDoublePercentSpecifier_returnParsed() {
        Optional<FmtExpression> expression = PARSER.parse("%%");

        assertThat(expression).hasValue(
                FmtExpression.builder()
                        .specifier(new FmtSpecifier(new Conversion("%"), new Position(0, 2)))
                        .build()
        );
    }

    @Test
    public void parse_specialNSpecifier_returnParsed() {
        Optional<FmtExpression> expression = PARSER.parse("%n");

        assertThat(expression).hasValue(
                FmtExpression.builder()
                        .specifier(new FmtSpecifier(new Conversion("n"), new Position(0, 2)))
                        .build()
        );
    }

    @Test
    public void parse_onlyText_returnParsed() {
        Optional<FmtExpression> expression = PARSER.parse("hello");

        assertThat(expression).hasValue(
                FmtExpression.builder()
                        .text(new FmtText("hello", new Position(0, 5)))
                        .build()
        );
    }

    @Test
    public void parse_simpleExpressionWithOneCodeBlockInBrackets_returnParsed() {
        Optional<FmtExpression> expression = PARSER.parse("Hello! My name is %s${name}");

        assertThat(expression).hasValue(
                FmtExpression.builder()
                        .text(new FmtText("Hello! My name is ", new Position(0, 18)))
                        .specifier(new FmtSpecifier(new Conversion("s"), new Position(18, 20)))
                        .code(new FmtCode("name", new Position(22, 26)))
                        .build()
        );
    }

    @Test
    public void parse_simpleExpressionWithOneCodeBlock_returnParsed() {
        Optional<FmtExpression> expression = PARSER.parse("Hello! My name is %s$name");

        assertThat(expression).hasValue(
                FmtExpression.builder()
                        .text(new FmtText("Hello! My name is ", new Position(0, 18)))
                        .specifier(new FmtSpecifier(new Conversion("s"), new Position(18, 20)))
                        .code(new FmtCode("name", new Position(21, 25)))
                        .build()
        );
    }

    @Test
    public void parse_specifierAndCodeAreSeparated_returnParsed() {
        Optional<FmtExpression> expression = PARSER.parse("Hello! My name is %d ${name}");

        assertThat(expression).hasValue(
                FmtExpression.builder()
                        .text(new FmtText("Hello! My name is ", new Position(0, 18)))
                        .specifier(new FmtSpecifier(new Conversion("d"), new Position(18, 20)))
                        .text(new FmtText(" ", new Position(20, 21)))
                        .code(new FmtCode("name", new Position(23, 27)))
                        .build()
        );
    }

    @Test
    public void parse_withExplicitPosition_returnParsed() {
        Optional<FmtExpression> expression = PARSER.parse("Hello! My name is %1$s${name}. Age is %2$d${age}");

        assertThat(expression).hasValue(
                FmtExpression.builder()
                        .text(new FmtText("Hello! My name is ", new Position(0, 18)))
                        .specifier(new FmtSpecifier(new NumericIndex(1), new Conversion("s"), new Position(18, 22)))
                        .code(new FmtCode("name", new Position(24, 28)))
                        .text(new FmtText(". Age is ", new Position(29, 38)))
                        .specifier(new FmtSpecifier(new NumericIndex(2), new Conversion("d"), new Position(38, 42)))
                        .code(new FmtCode("age", new Position(44, 47)))
                        .build()
        );
    }

    @Test
    public void parse_withImplicitPosition_returnParsed() {
        Optional<FmtExpression> expression = PARSER.parse("Hello! My name is %1$s${name}. Age is %<d${age}");

        assertThat(expression).hasValue(
                FmtExpression.builder()
                        .text(new FmtText("Hello! My name is ", new Position(0, 18)))
                        .specifier(new FmtSpecifier(new NumericIndex(1), new Conversion("s"), new Position(18, 22)))
                        .code(new FmtCode("name", new Position(24, 28)))
                        .text(new FmtText(". Age is ", new Position(29, 38)))
                        .specifier(new FmtSpecifier(ImplicitIndex.INSTANCE, new Conversion("d"), new Position(38, 41)))
                        .code(new FmtCode("age", new Position(43, 46)))
                        .build()
        );
    }
}