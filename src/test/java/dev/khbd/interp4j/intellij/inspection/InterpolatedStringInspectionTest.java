package dev.khbd.interp4j.intellij.inspection;

import dev.khbd.interp4j.intellij.BaseIntellijTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
        fixture.configureByFiles("inspection/wrong_string/Main.java");

        fixture.testHighlighting(true, false, true);
    }
}
