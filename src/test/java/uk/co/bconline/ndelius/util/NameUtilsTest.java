package uk.co.bconline.ndelius.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NameUtilsTest {

    @Test
    public void combineForenames() {
        String result = NameUtils.combineNames("a b", "c d");
        assertThat(result).isEqualTo("a b c d");
    }

    @Test
    public void combineForenamesWithNoSecondForename() {
        String result = NameUtils.combineNames("a b", null);
        assertThat(result).isEqualTo("a b");
    }

    @Test
    public void combineForenamesWithNoFirstForename() {
        String result = NameUtils.combineNames(null, "a");
        assertThat(result).isEqualTo(" a");
        assertThat(NameUtils.firstForename(result)).isEmpty();
        assertThat(NameUtils.subsequentForenames(result)).isEqualTo("a");
    }

    @Test
    public void combineLotsOfNames() {
        String result = NameUtils.combineNames("a b", "c d", "e f", "g h", "i j");
        assertThat(result).isEqualTo("a b c d e f g h i j");
    }

    @Test
    public void combineLotsOfNamesWithNulls() {
        String result = NameUtils.combineNames(null, "c d", "e f", null, "i j");
        assertThat(result).isEqualTo(" c d e f i j");
    }

    @Test
    public void firstForename() {
        String result = NameUtils.firstForename("a b c d");
        assertThat(result).isEqualTo("a");
    }

    @Test
    public void firstForenameWithNoSubsequentForename() {
        String result = NameUtils.firstForename("a");
        assertThat(result).isEqualTo("a");
    }

    @Test
    public void firstForenameWhenNull() {
        String result = NameUtils.firstForename(null);
        assertThat(result).isEmpty();
    }

    @Test
    public void subsequentForenames() {
        String result = NameUtils.subsequentForenames("a b c d");
        assertThat(result).isEqualTo("b c d");
    }

    @Test
    public void noSubsequentForeneames() {
        String result = NameUtils.subsequentForenames("a");
        assertThat(result).isEmpty();
    }

    @Test
    public void subsequentForenamesWhenNull() {
        String result = NameUtils.subsequentForenames(null);
        assertThat(result).isEmpty();
    }

}
