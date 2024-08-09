package dev.khbd.interp4j.intellij.inspection;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import dev.khbd.interp4j.intellij.BaseIntellijTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
public class StringFormatMightBeReplacedInspectionTest extends BaseIntellijTest {

    @BeforeEach
    public void initInspection() {
        fixture.enableInspections(StringFormatMightBeReplacedInspection.class);
    }

    @Test
    public void inspect_simpleStringFormatDetectedAndReplaceWithS_warnUsage() {
        fixture.configureByFiles("inspection/format/only_string_conversions/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(2);
        fixture.launchAction(findIntention(intentions, "`s`"));
        fixture.checkResultByFile("inspection/format/only_string_conversions/Main_after_s.java");
    }

    @Test
    public void inspect_simpleStringFormatDetectedAndReplaceWithFmt_warnUsage() {
        fixture.configureByFiles("inspection/format/only_string_conversions/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(2);
        fixture.launchAction(findIntention(intentions, "`fmt`"));
        fixture.checkResultByFile("inspection/format/only_string_conversions/Main_after_fmt.java");
    }

    @Test
    public void inspect_formatAsStaticField_notHighlight() {
        fixture.configureByFiles("inspection/format/format_as_static_field/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_complicatedFormat_notHighlight() {
        fixture.configureByFiles("inspection/format/complicated_format/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_withIntegerConversionAndReplaceWithS_warnUsage() {
        fixture.configureByFiles("inspection/format/with_integer_conversion/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(2);
        fixture.launchAction(findIntention(intentions, "`s`"));
        fixture.checkResultByFile("inspection/format/with_integer_conversion/Main_after_s.java");
    }

    @Test
    public void inspect_withIntegerConversionAndReplaceWithFmt_warnUsage() {
        fixture.configureByFiles("inspection/format/with_integer_conversion/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(2);
        fixture.launchAction(findIntention(intentions, "`fmt`"));
        fixture.checkResultByFile("inspection/format/with_integer_conversion/Main_after_fmt.java");
    }

    @Test
    public void inspect_wrongArgumentCount_notHighlight() {
        fixture.configureByFiles("inspection/format/wrong_argument_count/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_withSimpleConcatenationAndReplaceWithS_warnUsage() {
        fixture.configureByFiles("inspection/format/with_concatenation/simple_concat/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(2);
        fixture.launchAction(findIntention(intentions, "`s`"));

        fixture.checkResultByFile("inspection/format/with_concatenation/simple_concat/Main_after_s.java");
    }

    @Test
    public void inspect_withSimpleConcatenationAndReplaceWithFmt_warnUsage() {
        fixture.configureByFiles("inspection/format/with_concatenation/simple_concat/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(2);
        fixture.launchAction(findIntention(intentions, "`fmt`"));

        fixture.checkResultByFile("inspection/format/with_concatenation/simple_concat/Main_after_fmt.java");
    }

    @Test
    public void inspect_withConcatenationWhereMoreThanTwoPartsAndReplaceWithS_warnUsage() {
        fixture.configureByFiles("inspection/format/with_concatenation/more_than_two_parts/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(2);
        fixture.launchAction(findIntention(intentions, "`s`"));
        fixture.checkResultByFile("inspection/format/with_concatenation/more_than_two_parts/Main_after_s.java");
    }

    @Test
    public void inspect_withConcatenationWithNull_notHighlight() {
        fixture.configureByFiles("inspection/format/with_concatenation/with_null/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_withConcatenationWhereOnlyLiterals_notHighlight() {
        fixture.configureByFiles("inspection/format/with_concatenation/only_literals/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_withConcatenationWhereWrongArgumentCount_notHighlight() {
        fixture.configureByFiles("inspection/format/with_concatenation/wrong_argument_count/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_expressionWithDoubleQuotesAndReplaceWithS_warnUsage() {
        fixture.configureByFiles("inspection/format/expression_with_double_quotes/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(2);
        fixture.launchAction(findIntention(intentions, "`s`"));
        fixture.checkResultByFile("inspection/format/expression_with_double_quotes/Main_after_s.java");
    }

    @Test
    public void inspect_expressionWithDoubleQuotesAndReplaceWithFmt_warnUsage() {
        fixture.configureByFiles("inspection/format/expression_with_double_quotes/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(2);
        fixture.launchAction(findIntention(intentions, "`fmt`"));
        fixture.checkResultByFile("inspection/format/expression_with_double_quotes/Main_after_fmt.java");
    }

    private IntentionAction findIntention(List<IntentionAction> intentions, String text) {
        return intentions.stream()
                .filter(in -> in.getText().contains(text))
                .findFirst()
                .orElse(null);
    }
}
