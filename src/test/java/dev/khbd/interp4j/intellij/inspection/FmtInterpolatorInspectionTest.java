package dev.khbd.interp4j.intellij.inspection;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import dev.khbd.interp4j.intellij.BaseIntellijTest;
import dev.khbd.interp4j.intellij.inspection.validate.FmtInterpolatedStringInspectionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
public class FmtInterpolatorInspectionTest extends BaseIntellijTest {

    @BeforeEach
    public void initInspection() {
        fixture.enableInspections(FmtInterpolatedStringInspectionImpl.class);
    }

    @Test
    public void inspect_wrongInterpolatedString_verifyError() {
        fixture.configureByFiles("inspection/fmt/non_literal/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_stringLiteralsCanNotBeParsed_verifyError() {
        fixture.configureByFiles("inspection/fmt/can_not_parsed/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_stringWithoutAnyExpression_verifyWarn() {
        fixture.configureByFiles("inspection/fmt/without_any_expression/Main.java");

        fixture.testHighlighting(true, false, true);
        List<IntentionAction> intentions = fixture.getAllQuickFixes();
        assertThat(intentions).hasSize(2);
        launchActions(intentions);
        fixture.checkResultByFile("inspection/fmt/without_any_expression/Main_after.java");
    }

    @Test
    public void inspect_specifierWithoutCode_verifyError() {
        fixture.configureByFiles("inspection/fmt/specifier_no_code/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_codeWithSpecialSpecifiers_verifyError() {
        fixture.configureByFiles("inspection/fmt/code_with_special_specifiers/Main.java");

        fixture.testHighlighting(true, false, true);
    }

    @Test
    public void inspect_codeWithoutSpecifiers_verifyError() {
        fixture.configureByFiles("inspection/fmt/code_without_specifiers/Main.java");

        fixture.testHighlighting(true, false, true);
    }
}
