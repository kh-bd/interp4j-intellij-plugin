package dev.khbd.interp4j.intellij.inspection;

import dev.khbd.interp4j.intellij.BaseIntellijTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateMethodInvocationInspectionTest extends BaseIntellijTest {

    @BeforeMethod
    public void initInspection() {
        fixture.enableInspections(InterpolateMethodInvocationInspection.class);
    }

    @Test
    public void inspect_wrongInterpolatedString_verifyError() {
        fixture.configureByFiles("inspection/interpolate_method_invocation/Main.java");

        fixture.testHighlighting(true, false, true);
    }
}
