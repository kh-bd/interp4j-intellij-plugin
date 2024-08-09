package dev.khbd.interp4j.intellij.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author Sergei Khadanovich
 */
public class StringUtilsTest {

    @Test
    public void escapeDoubleQuotes_stringIsNull_returnNull() {
        String result = StringUtils.escapeDoubleQuotes(null);

        assertThat(result).isNull();
    }

    @Test
    public void escapeDoubleQuotes_stringIsEmpty_returnEmpty() {
        String result = StringUtils.escapeDoubleQuotes("");

        assertThat(result).isEmpty();
    }

    @Test
    public void escapeDoubleQuotes_stringHasNoQuotes_returnAsIs() {
        String result = StringUtils.escapeDoubleQuotes("Hello world!");

        assertThat(result).isEqualTo("Hello world!");
    }

    @Test
    public void escapeDoubleQuotes_stringHasQuotes_returnEscaped() {
        String result = StringUtils.escapeDoubleQuotes("Hello world!\"\"");

        assertThat(result).isEqualTo("Hello world!\\\"\\\"");
    }
}