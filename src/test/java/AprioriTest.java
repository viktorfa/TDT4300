import no.ntnu.idi.tdt4300.apriori.Apriori;
import no.ntnu.idi.tdt4300.apriori.ItemSet;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Naive testing of the output format. It doesn't test the correctness of the results!
 *
 * @author tdt4300-undass@idi.ntnu.no
 */
public class AprioriTest {

    private static final String[][] TRANSACTIONS = {
            {"bread", "milk"},
            {"beer", "bread", "diapers", "eggs"},
            {"beer", "cola", "diapers", "milk"},
            {"beer", "bread", "diapers", "milk"},
            {"bread", "cola", "diapers", "milk"},
            {"bread", "diapers", "milk"}
    };
    private static final double SUPPORT = 0.5;
    private static final double CONFIDENCE = 0.5;


    private static List<SortedSet<String>> transactions;

    private static double DELTA = 0.00001d;

    @BeforeClass
    public static void setUp() throws Exception {
        transactions = new ArrayList<SortedSet<String>>();
        for (String[] t : TRANSACTIONS) {
            transactions.add(new TreeSet<String>(Arrays.asList(t)));
        }
    }

    @Test
    public void testGenerateFrequentItemsets() throws Exception {
        String csvOutput = Apriori.generateFrequentItemsets(transactions, SUPPORT);
        System.out.println("Actual frequent item sets:");
        System.out.println(csvOutput);
        Pattern pattern = Pattern.compile("size;items\n(\\d+;([-_\\w]+,)*[-_\\w]+\n)*");
        assertTrue(pattern.matcher(csvOutput).matches());
    }

    @Test
    public void testGenerateAssociationRules() throws Exception {
        String csvOutput = Apriori.generateAssociationRules(transactions, SUPPORT, CONFIDENCE);
        System.out.println("Actual association rules:");
        System.out.println(csvOutput);
        Pattern pattern = Pattern.compile("antecedent;consequent;confidence;support\n(([-_\\w]+,)*[-_\\w]+;([-_\\w]+,)*[-_\\w]+;\\d.\\d{1,2};\\d.\\d{1,2}\n)*");
        assertTrue(pattern.matcher(csvOutput).matches());
    }

    @Test
    public void testGetOccurences() {
        Map<String, Set<Integer>> actual = Apriori.getOccurences(transactions);

        assertTrue(actual.containsKey("bread"));

        Set<Integer> expected = new TreeSet<>();

        expected.add(0);
        expected.add(1);
        expected.add(3);
        expected.add(4);
        expected.add(5);

        assertArrayEquals(actual.get("bread").toArray(), expected.toArray());
    }

    @Test
    public void testGetItemSetsWithSize1() {
        Set<ItemSet> actual = Apriori.getItemSets(transactions, new HashMap<ItemSet, Set<Integer>>(), 3, 0);

        ItemSet set1 = new ItemSet("bread");
        ItemSet set2 = new ItemSet("milk");
        ItemSet set3 = new ItemSet("eggs");

        System.out.println("Actual item sets:");
        System.out.println(actual);

        assertTrue(actual.contains(set1));
        assertTrue(actual.contains(set2));
        assertFalse(actual.contains(set3));
    }

    @Test
    public void testGetItemSetsWithSize2() {
        Set<ItemSet> actual = Apriori.getItemSets(transactions, new HashMap<ItemSet, Set<Integer>>(), 3, 2);

        ItemSet set1 = new ItemSet("bread");
        set1.addItem("milk");
        ItemSet set2 = new ItemSet("milk");
        set2.addItem("diapers");
        ItemSet set3 = new ItemSet("eggs");
        set3.addItem("diapers");

        System.out.println("Actual item sets:");
        System.out.println(actual);

        assertTrue(actual.contains(set1));
        assertTrue(actual.contains(set2));
        assertFalse(actual.contains(set3));
    }

    @Test
    public void testGetItemSetsWithSize3() {
        Set<ItemSet> actual = Apriori.getItemSets(transactions, new HashMap<ItemSet, Set<Integer>>(), 3, 3);
        System.out.println("Transactions:");
        System.out.println(transactions);

        ItemSet set1 = new ItemSet("bread");
        set1.addItem("milk");
        set1.addItem("diapers");
        ItemSet set2 = new ItemSet("milk");
        set2.addItem("diapers");
        set2.addItem("eggs");

        System.out.println("Actual item sets:");
        System.out.println(actual);
        System.out.println("set1:");
        System.out.println(set1);
        System.out.println("set2:");
        System.out.println(set2);

        assertTrue(actual.contains(set1));
        assertFalse(actual.contains(set2));
    }

    @Test
    public void testItemSetEquals() {
        ItemSet set1 = new ItemSet();
        ItemSet set2 = new ItemSet();

        set1.addItem("banana");
        set1.addItem("nutella");

        assertFalse(set1.equals(set2));

        set2.addItem("nutella");
        set2.addItem("banana");

        assertTrue(set1.equals(set2));

        set1.addItem("salt");

        assertFalse(set1.equals(set2));
    }

    @Test
    public void testItemSetHashCode() {
        ItemSet set1 = new ItemSet();
        ItemSet set2 = new ItemSet();

        set1.addItem("banana");
        set1.addItem("nutella");

        assertNotEquals(set1.hashCode(), set2.hashCode());

        set2.addItem("nutella");
        set2.addItem("banana");

        assertEquals(set1.hashCode(), set2.hashCode());

        set1.addItem("salt");

        assertNotEquals(set1.hashCode(), set2.hashCode());
    }
    
    @Test
    public void testIsNewItemSet() {
        Set<Integer> withBread = new TreeSet<>();
        withBread.add(0);
        withBread.add(1);
        withBread.add(3);
        withBread.add(4);
        withBread.add(5);

        Set<Integer> withMilk = new TreeSet<>();
        withMilk.add(0);
        withMilk.add(2);
        withMilk.add(3);
        withMilk.add(4);
        withMilk.add(5);

        Set<Integer> withBreadAndMilk = new TreeSet<>();
        withBreadAndMilk.add(0);
        withBreadAndMilk.add(3);
        withBreadAndMilk.add(4);
        withBreadAndMilk.add(5);

        Set<Integer> withEggs = new TreeSet<>();
        withEggs.add(1);

        
        assertTrue(Apriori.isNewItemSet(withBread, Apriori.getOccurences(transactions).get("milk"), 2));
        assertTrue(Apriori.isNewItemSet(withMilk, Apriori.getOccurences(transactions).get("diapers"), 2));
        assertTrue(Apriori.isNewItemSet(withBreadAndMilk, Apriori.getOccurences(transactions).get("diapers"), 2));
        assertFalse(Apriori.isNewItemSet(withEggs, Apriori.getOccurences(transactions).get("diapers"), 2));
    }

    @Test
    public void testCreateItemSetWithOldItemSet() {
        ItemSet set1 = new ItemSet("rat");
        set1.addItem("cow");

        assertEquals(set1.size(), 2);

        ItemSet set2 = new ItemSet(set1, "goat");
        assertEquals(set2.size(), 3);

        set1.addItem("goat");

        assertEquals(set1, set2);
    }

    @Test
    public void testSizeOfLargestSet() {
        List<SortedSet<String>> list = new LinkedList<>();
        SortedSet<String> set1 = new TreeSet<>();
        SortedSet<String> set2 = new TreeSet<>();
        SortedSet<String> set3 = new TreeSet<>();
        set1.add("a");
        set1.add("b");
        set1.add("c");
        set2.add("a");
        set2.add("c");
        list.add(set1);
        list.add(set2);
        list.add(set3);

        assertEquals(Apriori.sizeOfLargestSet(list), 3);
        list.remove(set1);
        assertEquals(Apriori.sizeOfLargestSet(list), 2);
        list.clear();
        assertEquals(Apriori.sizeOfLargestSet(list), 0);
    }

    @Test
    public void testPowerSet() {
        ItemSet itemSet1 = new ItemSet();
        itemSet1.addItem("eggs");
        itemSet1.addItem("bacon");
        itemSet1.addItem("chicken");

        Set<ItemSet> actual = Apriori.powerSet(itemSet1);

        System.out.println("actual power set:");
        System.out.println(actual);

        ItemSet expected1 = new ItemSet("eggs");
        ItemSet expected2 = new ItemSet(expected1, "bacon");
        ItemSet expected3 = new ItemSet(expected1, "chicken");
        ItemSet notExpected1 = new ItemSet(expected3, "maple syrup");

        assertTrue(actual.contains(expected1));
        assertTrue(actual.contains(expected2));
        assertTrue(actual.contains(expected3));
        assertFalse(actual.contains(notExpected1));

    }

    @Test
    public void testSupportCount() {
        ItemSet itemSet1 = new ItemSet();
        ItemSet itemSet2 = new ItemSet();

        itemSet1.addItem("beer");
        itemSet1.addItem("diapers");

        itemSet2.addItem("beer");

        double actual = Apriori.supportCount(itemSet1, itemSet2, transactions);

        assertEquals(1d, actual, DELTA);

        itemSet1.clear();
        itemSet2.clear();

        itemSet1.addItem("bread");
        itemSet1.addItem("milk");
        itemSet1.addItem("diapers");

        itemSet2.addItem("bread");
        itemSet2.addItem("milk");

        actual = Apriori.supportCount(itemSet1, itemSet2, transactions);
        assertEquals(0.75d, actual, DELTA);

        System.out.println(String.format("actual support count: %f", actual));
    }

    @Test
    public void testGetSupportOfItemSet() {
        ItemSet set1 = new ItemSet("cola");

        double actual = Apriori.getSupportOfItemSet(set1, transactions);

        assertEquals(2d / 6d, actual, DELTA);

        set1.addItem("beer");
        actual = Apriori.getSupportOfItemSet(set1, transactions);
        assertEquals(1d / 6d, actual, DELTA);
    }

}