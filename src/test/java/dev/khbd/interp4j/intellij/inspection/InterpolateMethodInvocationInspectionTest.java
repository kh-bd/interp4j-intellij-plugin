package dev.khbd.interp4j.intellij.inspection;

import dev.khbd.interp4j.intellij.BaseIntellijTest;
import dev.khbd.interp4j.intellij.inspection.validate.InterpolateMethodInvocationInspection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateMethodInvocationInspectionTest extends BaseIntellijTest {

    @BeforeEach
    public void initInspection() {
        fixture.enableInspections(InterpolateMethodInvocationInspection.class);
    }

    @Test
    public void inspect_wrongInterpolatedString_verifyError() {
        fixture.configureByFiles("inspection/interpolate_method_invocation/Main.java");

        fixture.testHighlighting(true, false, true);
    }
}
