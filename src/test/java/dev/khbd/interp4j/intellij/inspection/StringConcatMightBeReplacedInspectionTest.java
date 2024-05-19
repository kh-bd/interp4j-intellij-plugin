package dev.khbd.interp4j.intellij.inspection;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import dev.khbd.interp4j.intellij.BaseIntellijTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
public class StringConcatMightBeReplacedInspectionTest extends BaseIntellijTest {

    @BeforeMethod
    public void initInspection() {
        fixture.enableInspections(StringConcatMightBeReplacedInspection.class);
    }

    @Test
    public void inspect_concatenationInAnnotation_doNothing() {
        fixture.configureByFiles("inspection/concat/in_annotation/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_simpleStringConcatenation_warnExpression() {
        fixture.configureByFiles("inspection/concat/literal_and_expression/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(2);
        launchActions(intentions);
        fixture.checkResultByFile("inspection/concat/literal_and_expression/Main_after.java");
    }

    @Test
    public void inspect_onlyStringLiteralsInExpression_doNothing() {
        fixture.configureByFiles("inspection/concat/only_literals/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_withNullLiteral_doNothing() {
        fixture.configureByFiles("inspection/concat/with_null/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_moreThanTwoPartsInExpression_warnExpression() {
        fixture.configureByFiles("inspection/concat/more_than_two_parts/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(1);
        launchActions(intentions);
        fixture.checkResultByFile("inspection/concat/more_than_two_parts/Main_after.java");
    }

    @Test
    public void inspect_stringLiteralWithDoubleQuotes_warnExpression() {
        fixture.configureByFiles("inspection/concat/literal_with_double_quotes/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(1);
        launchActions(intentions);
        fixture.checkResultByFile("inspection/concat/literal_with_double_quotes/Main_after.java");
    }

    @Test
    public void inspect_expressionWithDoubleQuotes_warnExpression() {
        fixture.configureByFiles("inspection/concat/expression_with_double_quotes/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(1);
        launchActions(intentions);
        fixture.checkResultByFile("inspection/concat/expression_with_double_quotes/Main_after.java");
    }
}
