package expression;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NumericalAdjustmentTest {

    /**
     * When query string is null with valid args, an empty string should be returned.
     */
    @Test
    public void adjustNumericValueBy_QueryStringIsNull_ReturnsEmptyString() {
        List<Integer> relativeIndexes = new ArrayList<>();
        relativeIndexes.add(0);

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustNumericValueBy(null, "missing", relativeIndexes, 1);
        assertThat(result).isEmpty();
    }

    /**
     * When query string is empty with valid args, an empty string should be returned.
     */
    @Test
    public void adjustNumericValueBy_QueryStringIsEmpty_ReturnsEmptyString() {
        List<Integer> relativeIndexes = new ArrayList<>();
        relativeIndexes.add(0);

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustNumericValueBy("", "missing", relativeIndexes, 1);
        assertThat(result).isEmpty();
    }

    /**
     * When the key is missing, the original query string should be returned.
     */
    @Test
    public void adjustNumericValueBy_MissingKey_HasNoEffect() {
        String query = "key4=abc&key6=xyz&key8=100";

        List<Integer> relativeIndexes = new ArrayList<>();
        relativeIndexes.add(0);

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustNumericValueBy(query, "missing", relativeIndexes, 1);
        assertThat(result).isEqualTo(query);
    }

    /**
     * When the value type is not an integer it should be ignored and the original query string returned.
     */
    @Test
    public void adjustNumericValueBy_IncompatibleValueType() {
        String query = "key4=abc&key6=xyz&key8=100";

        List<Integer> relativeIndexes = new ArrayList<>();
        relativeIndexes.add(0);

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustNumericValueBy(query, "key6", relativeIndexes, 1);
        assertThat(result).isEqualTo(query);
    }

    /**
     * When the value type is an integer it should be adjusted by the given value.
     * <p>
     * Adjust by 0 = no change and return original query string
     */
    @Test
    public void adjustNumericValueBy_ByValueIsZero_HasNoEffect() {
        String query = "key4=abc&key6=xyz&key8=100&key9=fgy";

        List<Integer> relativeIndexes = new ArrayList<>();
        relativeIndexes.add(0);

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustNumericValueBy(query, "key8", relativeIndexes, 0);
        assertThat(result).isEqualTo(query);
    }

    /**
     * When the value type is an integer it should be adjusted by the given value.
     * <p>
     * Adjust by 5 = increment by 5 given 5 is a positive number.
     */
    @Test
    public void adjustNumericValueBy_IncrementsByValue() {
        String query = "key4=abc&key6=xyz&key8=100&key9=fgy";
        String expected = "key4=abc&key6=xyz&key8=105&key9=fgy";

        List<Integer> relativeIndexes = new ArrayList<>();
        relativeIndexes.add(0);

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustNumericValueBy(query, "key8", relativeIndexes, 5);
        assertThat(result).isEqualTo(expected);
    }

    /**
     * When the value type is an integer it should be adjusted by the given value.
     * <p>
     * Adjust by -5 = decrement by 5 given 5 is a negative number.
     */
    @Test
    public void adjustNumericValueBy_DecrementsByValue() {
        String query = "key4=abc&key6=xyz&key8=100&key9=fgy";
        String expected = "key4=abc&key6=xyz&key8=95&key9=fgy";

        List<Integer> relativeIndexes = new ArrayList<>();
        relativeIndexes.add(0);

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustNumericValueBy(query, "key8", relativeIndexes, -5);
        assertThat(result).isEqualTo(expected);
    }

    /**
     * When multiple relative indexes are provided, the value is updated by the new amount only if its numeric
     * otherwise it should be ignored.
     * <p>
     * Eg: key8 = [100, 23, 'hello_world-53', -5, '23-about', 80]
     * <p>
     * This test updates index 1, 2, 3, 4 to make sure it only selectively updates numeric values.
     */
    @Test
    public void adjustNumericValueBy_ModifiesMultipleNumericKeys() {
        String query = "key4=ab%20c&key8=100&key9=fgy&key8=23&key11=50&key8=hello_world-53&key8=-5&key8=23-about&key8=80";
        String expected = "key4=ab%20c&key8=100&key9=fgy&key8=24&key11=50&key8=hello_world-53&key8=-4&key8=23-about&key8=80";

        List<Integer> relativeIndexes = new ArrayList<>();
        relativeIndexes.add(1);
        relativeIndexes.add(2);
        relativeIndexes.add(3);
        relativeIndexes.add(4);

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustNumericValueBy(query, "key8", relativeIndexes, 1);
        assertThat(result).isEqualTo(expected);
    }

    /**
     * When all relative indexes are provided, the value is updated by the new amount only if its numeric
     * otherwise it should be ignored. Indexes outside of array bounds should be ignored.
     * <p>
     * Eg: key8 = [100, 23, 'hello_world-53', -5, '23-about', 80]
     */
    @Test
    public void adjustNumericValueBy_ModifiesAllNumericKeys_InvalidIndexesIgnored() {
        String query = "key4=ab%20c&key8=100&key9=fgy&key8=23&key11=50&key8=hello_world-53&key8=-5&key8=23-about&key8=80";
        String expected = "key4=ab%20c&key8=101&key9=fgy&key8=24&key11=50&key8=hello_world-53&key8=-4&key8=23-about&key8=81";

        List<Integer> relativeIndexes = new ArrayList<>();
        relativeIndexes.add(-1);
        relativeIndexes.add(0);
        relativeIndexes.add(1);
        relativeIndexes.add(2);
        relativeIndexes.add(3);
        relativeIndexes.add(4);
        relativeIndexes.add(5);
        relativeIndexes.add(6);

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustNumericValueBy(query, "key8", relativeIndexes, 1);
        assertThat(result).isEqualTo(expected);
    }

    /**
     * When the target key has all non numeric values they should be ignored with no effect.
     */
    @Test
    public void adjustNumericValueBy_HasNoEffect_WhenAllValuesNonNumeric() {
        String query = "key4=a&key4=b&key4=c&key5=d&key7=e";

        // set key4 [a, b, c] to increment by 1. It should do nothing given they are non numeric
        List<Integer> relativeIndexes = new ArrayList<>();
        relativeIndexes.add(-1);
        relativeIndexes.add(0);
        relativeIndexes.add(1);
        relativeIndexes.add(2);
        relativeIndexes.add(3);

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustNumericValueBy(query, "key4", relativeIndexes, 1);
        assertThat(result).isEqualTo(query);
    }

    @Test
    public void adjustFirstNumericValueBy_QueryStringIsNull_ReturnEmptyString() {
        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustFirstNumericValueBy(null, "key2", 3);
        assertThat(result).isEmpty();
    }

    @Test
    public void adjustFirstNumericValueBy_QueryStringIsEmpty_ReturnEmptyString() {
        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustFirstNumericValueBy("", "key2", 3);
        assertThat(result).isEmpty();
    }

    /**
     * When the key is missing, the original query string should be returned.
     */
    @Test
    public void adjustFirstNumericValueBy_MissingKey_HasNoEffect() {
        String query = "key4=abc&key6=xyz&key8=100";

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustFirstNumericValueBy(query, "key80", 3);
        assertThat(result).isEqualTo(query);
    }

    /**
     * When the value type is not an integer it should be ignored and the original query string returned.
     */
    @Test
    public void adjustFirstNumericValueBy_IncompatibleValueType() {
        String query = "key4=abc&key6=xyz&key8=100";

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustFirstNumericValueBy(query, "key6", 3);
        assertThat(result).isEqualTo(query);
    }

    /**
     * When the value type is an integer it should be adjusted by the given value.
     * <p>
     * Adjust by 0 = no change and return original query string
     */
    @Test
    public void adjustFirstNumericValueBy_ByValueIsZero_HasNoEffect() {
        String query = "key4=abc&key6=xyz&key8=100&key9=fgy";

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustFirstNumericValueBy(query, "key8", 0);
        assertThat(result).isEqualTo(query);
    }

    /**
     * When the value type is an integer it should be adjusted by the given value.
     * <p>
     * Adjust by 5 = increment by 5 given 5 is a positive number.
     */
    @Test
    public void adjustFirstNumericValueBy_IncrementsByValue() {
        String query = "key4=abc&key6=xyz&key8=100&key9=fgy";
        String expected = "key4=abc&key6=xyz&key8=103&key9=fgy";

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustFirstNumericValueBy(query, "key8", 3);
        assertThat(result).isEqualTo(expected);
    }

    /**
     * When the value type is an integer it should be adjusted by the given value.
     * <p>
     * Adjust by -5 = decrement by 5 given 5 is a negative number.
     */
    @Test
    public void adjustFirstNumericValueBy_DecrementsByValue() {
        String query = "key4=abc&key6=xyz&key8=100&key9=fgy";
        String expected = "key4=abc&key6=xyz&key8=97&key9=fgy";

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustFirstNumericValueBy(query, "key8", -3);
        assertThat(result).isEqualTo(expected);
    }

    /**
     * When multiple relative indexes are provided, the value is updated only for the first key with all subsequent
     * values ignored.
     * <p>
     * Eg: key8 = [100, 23, 'hello_world-53', -5, '23-about', 80]
     * <p>
     * This tests to ensure only the first occurrence of key8 is updated.
     */
    @Test
    public void adjustFirstNumericValueBy_ShouldOnlyUpdateFirstKey() {
        String query = "key4=ab%20c&key8=100&key9=fgy&key8=23&key11=50&key8=hello_world-53&key8=-5&key8=23-about&key8=80";
        String expected = "key4=ab%20c&key8=103&key9=fgy&key8=23&key11=50&key8=hello_world-53&key8=-5&key8=23-about&key8=80";

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustFirstNumericValueBy(query, "key8", 3);
        assertThat(result).isEqualTo(expected);
    }

    /**
     * Sanity check to ensure nothing is changed when all keys are non numeric
     */
    @Test
    public void adjustFirstNumericValueBy_NoEffect_WhenAllValuesAreNonNumeric() {
        String query = "key4=ab%20c&key8=x100&key9=fgy&key8=x23&key11=x50&key8=hello_world-53&key8=-5&key8=23-about&key8=x80";

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.adjustFirstNumericValueBy(query, "key8", 3);
        assertThat(result).isEqualTo(query);
    }
}
