package dev.khbd.interp4j.intellij.inspection;

import dev.khbd.interp4j.intellij.BaseIntellijTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class StringFormatInspectionTest extends BaseIntellijTest {

    @BeforeMethod
    public void initInspection() {
        fixture.enableInspections(StringFormatMightBeReplacedInspection.class);
    }

    @Test
    public void inspect_simpleStringFormatDetected_warnUsage() {
        fixture.configureByFiles("inspection/format/only_string_conversions/Main.java");

        fixture.testHighlighting(true, false, true);
    }

}
