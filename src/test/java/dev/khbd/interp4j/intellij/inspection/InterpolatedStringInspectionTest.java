package dev.khbd.interp4j.intellij.inspection;

import com.intellij.codeInsight.intention.IntentionAction;
import dev.khbd.interp4j.intellij.BaseIntellijTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolatedStringInspectionTest extends BaseIntellijTest {

    @BeforeMethod
    public void initInspection() {
        fixture.enableInspections(InterpolatedStringInspection.class);
    }

    @Test
    public void inspect_wrongInterpolatedString_verifyError() {
        fixture.configureByFiles("inspection/non_literal/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_stringLiteralsCanNotBeParsed_verifyError() {
        fixture.configureByFiles("inspection/can_not_parsed/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_stringWithoutAnyExpression_verifyWarn() {
        fixture.configureByFiles("inspection/without_any_expression/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(3);
        launchActions(intentions);
        fixture.checkResultByFile("inspection/without_any_expression/Main_after.java");
    }
}
