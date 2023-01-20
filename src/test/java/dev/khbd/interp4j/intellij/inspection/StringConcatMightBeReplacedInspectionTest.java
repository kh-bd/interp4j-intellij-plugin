package dev.khbd.interp4j.intellij.inspection;

import dev.khbd.interp4j.intellij.BaseIntellijTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class StringConcatMightBeReplacedInspectionTest extends BaseIntellijTest {

    @BeforeMethod
    public void initInspection() {
        fixture.enableInspections(StringConcatMightBeReplacedInspection.class);
    }

    @Test
    public void inspect_simpleStringConcatenation_warnExpression() {
        fixture.configureByFiles("inspection/concat/literal_and_expression/Main.java");

        fixture.testHighlighting(true, false, true);
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
}
