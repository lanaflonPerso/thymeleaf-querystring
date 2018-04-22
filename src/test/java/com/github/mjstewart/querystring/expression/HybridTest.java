package com.github.mjstewart.querystring.expression;

import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class HybridTest {

    /**
     * When the query string is null and no new key value pairs are being added, an empty string should be returned.
     */
    @Test
    public void removeAllAndAdd_QueryStringIsNull_NothingNewToAdd_ReturnsEmptyString() {
        QueryStringHelper helper = new QueryStringHelper();

        List<String> removeKeys = new ArrayList<>();
        removeKeys.add("key2");
        List<List<String>> addKeyValues = new ArrayList<>();

        String result = helper.removeAllAndAdd(null, removeKeys, addKeyValues);
        assertThat(result).isEmpty();
    }

    /**
     * When the query string is empty and no new key value pairs are being added, an empty string should be returned.
     */
    @Test
    public void removeAllAndAdd_QueryStringIsEmpty_NothingNewToAdd_ReturnsEmptyString() {
        QueryStringHelper helper = new QueryStringHelper();

        List<String> removeKeys = new ArrayList<>();
        removeKeys.add("key2");
        List<List<String>> addKeyValues = new ArrayList<>();

        String result = helper.removeAllAndAdd("", removeKeys, addKeyValues);
        assertThat(result).isEmpty();
    }

    /**
     * When the query string is null but there are new key value pairs being added, the query string should consist
     * of only the new key value pairs in the exact order they are in the add list.
     */
    @Test
    public void removeAllAndAdd_QueryStringIsNull_WithNewValues_NewValuesAdded() {
        QueryStringHelper helper = new QueryStringHelper();

        String expected = "city=san%20francisco&region=east%20south%20west&id=49082.2334-3";

        List<String> removeKeys = new ArrayList<>();
        removeKeys.add("key2");

        List<List<String>> addKeyValues = new ArrayList<>();
        addKeyValues.add(Arrays.asList("city", "san francisco"));
        addKeyValues.add(Arrays.asList("region", "east south west"));
        addKeyValues.add(Arrays.asList("id", "49082.2334-3"));

        String result = helper.removeAllAndAdd(null, removeKeys, addKeyValues);
        assertThat(result).isEqualTo(expected);
    }

    /**
     * When the query string is null but there are new key value pairs being added, the query string should consist
     * of only the new key value pairs in the exact order they are in the add list.
     */
    @Test
    public void removeAllAndAdd_QueryStringIsEmpty_WithNewValues_NewValuesAdded() {
        QueryStringHelper helper = new QueryStringHelper();

        String expected = "city=san%20francisco&region=east%20south%20west&id=49082.2334-3";

        List<String> removeKeys = new ArrayList<>();
        removeKeys.add("key2");

        List<List<String>> addKeyValues = new ArrayList<>();
        addKeyValues.add(Arrays.asList("city", "san francisco"));
        addKeyValues.add(Arrays.asList("region", "east south west"));
        addKeyValues.add(Arrays.asList("id", "49082.2334-3"));

        String result = helper.removeAllAndAdd("", removeKeys, addKeyValues);
        assertThat(result).isEqualTo(expected);
    }

    /**
     * When the arguments are empty, the original query string should be returned.
     */
    @Test
    public void removeAllAndAdd_EmptyRemoveAndAddLists_HasNoEffect() {
        String query = "key4=ValueA&key2=ValueB&key3=ValueC";

        QueryStringHelper helper = new QueryStringHelper();

        List<String> removeKeys = new ArrayList<>();
        List<List<String>> addKeyValues = new ArrayList<>();

        String result = helper.removeAllAndAdd(query, removeKeys, addKeyValues);
        assertThat(result).isEqualTo(query);
    }

    /**
     * When the remove key is not found, the new key/value pair should still be added to the end.
     */
    @Test
    public void removeAllAndAdd_RemoveKeyNotFound_StillAdd_WithEscaping() {
        String query = "key4=ValueA&key2=ValueB&key3=ValueC";
        String expected = "key4=ValueA&key2=ValueB&key3=ValueC&keyNew=New%20Value%20%20";

        QueryStringHelper helper = new QueryStringHelper();

        List<String> removeKeys = Arrays.asList("missing");
        List<List<String>> addKeyValues = new ArrayList<>();
        addKeyValues.add(Arrays.asList("keyNew", "New Value  "));

        String result = helper.removeAllAndAdd(query, removeKeys, addKeyValues);
        assertThat(result).isEqualTo(expected);
    }

    /**
     * When all remove keys are not found, the new key/value pair should be added to the end.
     */
    @Test
    public void removeAllAndAdd_AllRemoveKeysNotFound_StillAdd() {
        String query = "key4=ValueA&key2=ValueB&key3=ValueC";
        String expected = "key4=ValueA&key2=ValueB&key3=ValueC&keyNew=New%20Value";

        QueryStringHelper helper = new QueryStringHelper();

        List<String> removeKeys = Arrays.asList("missing1", "missing2", "missing3");
        List<List<String>> addKeyValues = new ArrayList<>();
        addKeyValues.add(Arrays.asList("keyNew", "New Value"));

        String result = helper.removeAllAndAdd(query, removeKeys, addKeyValues);
        assertThat(result).isEqualTo(expected);
    }

    /**
     * When the remove key is found, ALL occurrences of the remove key shall be removed.
     * The new key,value pair should also be added to the end.
     */
    @Test
    public void removeAllAndAdd_SingleRemoveAndAddKeys() {
        String query = "key4=ValueA&key2=ValueB&key3=ValueC&key4=ValueX&key4=ValueY";
        String expected = "key2=ValueB&key3=ValueC&keyNew=New%20Value";

        // Note: 1 remove key and 1 add key/value pair.
        List<String> removeKeys = Arrays.asList("key4");
        List<List<String>> addKeyValues = new ArrayList<>();
        addKeyValues.add(Arrays.asList("keyNew", "New Value"));

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.removeAllAndAdd(query, removeKeys, addKeyValues);

        assertThat(result).isEqualTo(expected);
    }

    /**
     * When the remove key is found, ALL occurrences of the remove key shall be removed with missing keys ignored.
     * The new key,value pair should be added to the end.
     */
    @Test
    public void removeAllAndAdd_ManyRemoveAndAddKeys() {
        String query = "key4=ValueA&key4=ValueAA&key2=ValueB&key3=ValueC&key4=ValueX&key4=ValueY&key2=YX3.23b";
        String expected = "key3=ValueC&keyNew2=newValue2&keyNew3=newValue%203";

        // Note: 3 remove keys and 2 add key/value pair.
        List<String> removeKeys = Arrays.asList("key4", "missing", "key2");
        List<List<String>> addKeyValues = new ArrayList<>();
        addKeyValues.add(Arrays.asList("keyNew2", "newValue2"));
        addKeyValues.add(Arrays.asList("keyNew3", "newValue 3"));

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.removeAllAndAdd(query, removeKeys, addKeyValues);

        assertThat(result).isEqualTo(expected);
    }

    /**
     * Check that whitespace is escaped etc.
     */
    @Test
    public void removeAllAndAdd_ManyRemoveAndAddKeys_WithEscaping() {
        String query = "key4=ValueA&key4=ValueAA&key2=ValueB&key3=ValueC&key4=ValueX&key4=ValueY&key2=YX3.23b";
        String expected = "key3=ValueC&keyNew2=%20%20New%20Value%202&keyNew3=%20%20New%20.%20%20Value%203%20";

        // Note: 2 remove keys with 1 missing and add 2 key/value pairs.
        List<String> removeKeys = Arrays.asList("key4", "missing", "key2");
        List<List<String>> addKeyValues = new ArrayList<>();
        addKeyValues.add(Arrays.asList("keyNew2", "  New Value 2"));
        addKeyValues.add(Arrays.asList("keyNew3", "  New .  Value 3 "));

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.removeAllAndAdd(query, removeKeys, addKeyValues);

        assertThat(result).isEqualTo(expected);
    }

    /**
     * When all keys are removed, the resulting query string should consist of only the newly added key value pairs.
     */
    @Test
    public void removeAllAndAdd_AllKeysRemoved_ResultIsOnlyTheAddedPairs() {
        String query = "key4=ValueA&key4=ValueAA&key2=ValueB&key3=ValueC&key4=ValueX&key4=ValueY&key2=YX3.23b";
        String expected = "keyNew2=%20%20New%20Value%202&keyNew3=%20%20New%20.%20%20Value%203%20";

        List<String> removeKeys = Arrays.asList("key4", "key3", "key2");
        List<List<String>> addKeyValues = new ArrayList<>();
        addKeyValues.add(Arrays.asList("keyNew2", "  New Value 2"));
        addKeyValues.add(Arrays.asList("keyNew3", "  New .  Value 3 "));

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.removeAllAndAdd(query, removeKeys, addKeyValues);

        assertThat(result).isEqualTo(expected);
    }

    /**
     * When all keys are removed and there are no key value pairs being added, the result should
     * be an empty string.
     */
    @Test
    public void removeAllAndAdd_AllKeysRemoved_NoKeyValuesToAdd_IsEmptyString() {
        String query = "key4=ValueA&key4=ValueAA&key2=ValueB&key3=ValueC&key4=ValueX&key4=ValueY&key2=YX3.23b";

        List<String> removeKeys = Arrays.asList("key4", "key3", "key2");
        List<List<String>> addKeyValues = new ArrayList<>();

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.removeAllAndAdd(query, removeKeys, addKeyValues);

        assertThat(result).isEmpty();
    }

    /**
     * When the query string is null and there is nothing to add, an empty string should be returned.
     */
    @Test
    public void removeNthAndAdd_QueryStringIsNull_NothingToAdd_ReturnEmptyString() {
        Map<String, List<Integer>> removeInstructions = new HashMap<>();
        removeInstructions.put("key2", Arrays.asList(1, 2));

        List<List<String>> addKeyValuePairs = new ArrayList<>();

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.removeNthAndAdd(null, removeInstructions, addKeyValuePairs);

        assertThat(result).isEmpty();
    }

    /**
     * When the query string is empty and there is nothing to add, an empty string should be returned.
     */
    @Test
    public void removeNthAndAdd_QueryStringIsEmpty_NothingToAdd_ReturnEmptyString() {
        Map<String, List<Integer>> removeInstructions = new HashMap<>();
        removeInstructions.put("key2", Arrays.asList(1, 2));

        List<List<String>> addKeyValuePairs = new ArrayList<>();

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.removeNthAndAdd("", removeInstructions, addKeyValuePairs);

        assertThat(result).isEmpty();
    }

    /**
     * When the arguments are empty, the original query string should be returned.
     */
    @Test
    public void removeNthAndAdd_HandlesEmptyArgs_HasNoEffect() {
        String query = "key4=aa&key2=bb&key3=cc&key2=dd&key4=ee&key2=ff&key4=hh&key2=ii&key3=jj&key9=kk";

        Map<String, List<Integer>> removeInstructions = new HashMap<>();
        List<List<String>> addKeyValuePairs = new ArrayList<>();

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.removeNthAndAdd(query, removeInstructions, addKeyValuePairs);

        assertThat(result).isEqualTo(query);
    }

    /**
     * Remove a single keys value at some legal relative index then add new key/value pair.
     *
     * Consider key2, the relative indexes are [bb, dd, ff, ii]. This test is to check that
     * removing a relative index somewhere other than the first index such 'ff' actually works and is followed by adding.
     */
    @Test
    public void removeNthAndAdd_RemovesOne_AddsOne() {
        String query = "key4=aa&key2=bb&key3=cc&key2=dd&key4=ee&key2=ff&key4=hh&key2=ii&key3=jj&key9=kk";
        String expected = "key4=aa&key2=bb&key3=cc&key2=dd&key4=ee&key4=hh&key2=ii&key3=jj&key9=kk&key100=New%20Value";

        // simulates spel expression
        Map<String, List<Integer>> removeInstructions = new HashMap<>();
        // Remove value for key2 at relative index 2
        removeInstructions.put("key2", Collections.singletonList(2));

        List<List<String>> addKeyValuePairs = new ArrayList<>();
        addKeyValuePairs.add(Arrays.asList("key100", "New Value"));

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.removeNthAndAdd(query, removeInstructions, addKeyValuePairs);

        assertThat(result).isEqualTo(expected);
    }

    /**
     * If key2 is considered, the relative indexes are [bb, dd, ff, ii]. This test is to check that
     * removing invalid indexes has no effect and is followed by adding the new key value pair with escaping.
     */
    @Test
    public void removeNthAndAdd_InvalidRemovalIndexesHasNoEffect_ButStillAddsWithEscaping() {
        String query = "key4=aa&key2=bb&key3=cc&key2=dd&key4=ee&key2=ff&key4=hh&key2=ii&key3=jj&key9=kk";
        String expected = "key4=aa&key2=bb&key3=cc&key2=dd&key4=ee&key2=ff&key4=hh&key2=ii&key3=jj&key9=kk&key100=%20New%20Value%20";

        // simulates spel expression
        Map<String, List<Integer>> removeInstructions = new HashMap<>();
        // Remove value for key2 at relative index -1, 4, -100, 549. Illegal indexes should be ignored.
        removeInstructions.put("key2", Arrays.asList(-1, 4, -100, 549));

        List<List<String>> addKeyValuePairs = new ArrayList<>();
        addKeyValuePairs.add(Arrays.asList("key100", " New Value "));

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.removeNthAndAdd(query, removeInstructions, addKeyValuePairs);

        assertThat(result).isEqualTo(expected);
    }

    /**
     * If key2 is considered, the relative indexes are [bb, dd, ff, ii]. This test is to check that
     * removing many keys + relative indexes actually works and is followed by adding many key/values.
     */
    @Test
    public void removeNthAndAdd_RemovesMany_AddsMany() {
        String query = "key4=aa&key2=bb&key3=cc&key2=dd&key4=ee&key2=ff&key4=hh&key2=ii&key4=vv&key3=jj&key9=kk";
        String expected = "key4=aa&key3=cc&key2=dd&key2=ff&key4=vv&key3=jj&key9=kk&key100=New%20Value";

        // simulates spel expression
        Map<String, List<Integer>> removeInstructions = new HashMap<>();
        // Remove values for key2 at relative index 0 and 3
        removeInstructions.put("key2", Arrays.asList(0, 3));
        // Remove values for key2 at relative index 1 and 2
        removeInstructions.put("key4", Arrays.asList(1, 2));

        List<List<String>> addKeyValuePairs = new ArrayList<>();
        addKeyValuePairs.add(Arrays.asList("key100", "New Value"));

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.removeNthAndAdd(query, removeInstructions, addKeyValuePairs);

        assertThat(result).isEqualTo(expected);
    }

    /**
     * Sanity check - when all keys and relative indexes are removed, the result should be whatever is being
     * added (in this test nothing is being added so it should be an empty string).
     *
     * Note: The last 2 values of each removeInstruction is an upper and lower bound check on the expected
     * relative index positions. This is to ensure illegal indexes are ignored.
     *
     * Eg consider key2. key2 has values [bb, dd, ff, ii, kk]. The relative indexes are the array indexes 0 to 4.
     * Therefore the last 2 values in the removeInstruction are 4 (upper bound) and -1 (lower bound).
     */
    @Test
    public void removeNthAndAdd_RemovesAll_WithNothingToAdd() {
        String query = "key4=aa&key2=bb&key3=cc&key2=dd&key4=ee&key2=ff&key4=hh&key2=ii&key4=vv&key3=jj&key9=kk";

        // simulates spel expression
        Map<String, List<Integer>> removeInstructions = new HashMap<>();
        removeInstructions.put("key2", Arrays.asList(0, 1, 2, 3, 4, -1));
        removeInstructions.put("key4", Arrays.asList(0, 1, 2, 3, 4, -1));
        removeInstructions.put("key3", Arrays.asList(0, 1, 2, -1));
        removeInstructions.put("key9", Arrays.asList(0, 1, -1));

        List<List<String>> addKeyValuePairs = new ArrayList<>();

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.removeNthAndAdd(query, removeInstructions, addKeyValuePairs);

        assertThat(result).isEmpty();
    }

    /**
     * Sanity check - when all keys and relative indexes are removed, the result should be whatever is being
     * added. This also checks to ensure the added key/values are escaped correctly in the final query string.
     *
     * lower -1 and upper bound (size + 1) checks are also included in the last 2 index positions.
     * This is to ensure illegal indexes are ignored.
     */
    @Test
    public void removeNthAndAdd_RemovesAll_ThenAdds_WithEscaping() {
        String query = "key4=aa&key2=bb&key3=cc&key2=dd&key4=ee&key2=ff&key4=hh&key2=ii&key4=vv&key3=jj&key9=kk";
        String expected = "key500=Key%20500%20Value&key600=Key%20600%20Value";

        // simulates spel expression
        Map<String, List<Integer>> removeInstructions = new HashMap<>();
        removeInstructions.put("key2", Arrays.asList(0, 1, 2, 3, 4, -1));
        removeInstructions.put("key4", Arrays.asList(0, 1, 2, 3, 4, -1));
        removeInstructions.put("key3", Arrays.asList(0, 1, 2, -1));
        removeInstructions.put("key9", Arrays.asList(0, 1, -1));

        List<List<String>> addKeyValuePairs = new ArrayList<>();
        addKeyValuePairs.add(Arrays.asList("key500", "Key 500 Value"));
        addKeyValuePairs.add(Arrays.asList("key600", "Key 600 Value"));

        QueryStringHelper helper = new QueryStringHelper();
        String result = helper.removeNthAndAdd(query, removeInstructions, addKeyValuePairs);

        assertThat(result).isEqualTo(expected);
    }
}
