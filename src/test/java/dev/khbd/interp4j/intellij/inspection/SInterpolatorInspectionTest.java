package dev.khbd.interp4j.intellij.inspection;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import dev.khbd.interp4j.intellij.BaseIntellijTest;
import dev.khbd.interp4j.intellij.inspection.validate.SInterpolatedStringInspectionImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
public class SInterpolatorInspectionTest extends BaseIntellijTest {

    @BeforeMethod
    public void initInspection() {
        fixture.enableInspections(SInterpolatedStringInspectionImpl.class);
    }

    @Test
    public void inspect_wrongInterpolatedString_verifyError() {
        fixture.configureByFiles("inspection/s/non_literal/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_stringLiteralsCanNotBeParsed_verifyError() {
        fixture.configureByFiles("inspection/s/can_not_parsed/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_stringWithoutAnyExpression_verifyWarn() {
        fixture.configureByFiles("inspection/s/without_any_expression/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(3);
        launchActions(intentions);
        fixture.checkResultByFile("inspection/s/without_any_expression/Main_after.java");
    }
}
