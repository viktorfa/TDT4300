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
        Pattern pattern = Pattern.compile("size;items\n(\\d+;([-_\\w]+,)*[-_\\w]+\n)*");
        assertTrue(pattern.matcher(csvOutput).matches());
    }

    @Test
    public void testGenerateAssociationRules() throws Exception {
        String csvOutput = Apriori.generateAssociationRules(transactions, SUPPORT, CONFIDENCE);
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

        ItemSet set1 = new ItemSet("bread");
        set1.addItem("milk");
        set1.addItem("diapers");
        ItemSet set2 = new ItemSet("milk");
        set2.addItem("diapers");
        set2.addItem("eggs");

        System.out.println("Actual item sets:");
        for (ItemSet itemSet : actual) {
            System.out.println(itemSet.size());
        }

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
        withMilk.add(0);
        withMilk.add(3);
        withMilk.add(4);
        withMilk.add(5);

        Set<Integer> withEggs = new TreeSet<>();
        withEggs.add(1);

        
        assertTrue(Apriori.isNewItemSet(withBread, Apriori.getOccurences(transactions).get("milk"), 2));
        assertTrue(Apriori.isNewItemSet(withMilk, Apriori.getOccurences(transactions).get("diapers"), 2));
        assertTrue(Apriori.isNewItemSet(withBreadAndMilk, Apriori.getOccurences(transactions).get("diapers"), 2));
        assertFalse(Apriori.isNewItemSet(withEggs, Apriori.getOccurences(transactions).get("diapers"), 2));
    }

}