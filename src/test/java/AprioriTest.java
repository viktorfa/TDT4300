import no.ntnu.idi.tdt4300.apriori.Apriori;
import no.ntnu.idi.tdt4300.apriori.FPTree;
import no.ntnu.idi.tdt4300.apriori.Item;
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
    public void testCreateFPTree() {
        FPTree tree1 = new FPTree();
        assertNotNull(tree1.children);

        FPTree tree2 = new FPTree("bread", tree1);
        assertNotNull(tree2.children);
        assertEquals(tree2.count, 1);
        assertEquals(tree2.item, "bread");
    }

    @Test
    public void testInsertTree() {
        FPTree tree1 = new FPTree();

        SortedSet<String> transaction = new TreeSet<>();
        transaction.add("bread");
        transaction.add("beer");
        transaction.add("milk");

        Map<String, Integer> frequencyMap = new TreeMap<>();
        frequencyMap.put("bread", 2);
        frequencyMap.put("beer", 1);
        frequencyMap.put("milk", 1);

        SortedSet<Item> sortedTransaction = Apriori.getSortedTransaction(transaction, frequencyMap);

        Apriori.insertTree(sortedTransaction, tree1);

        assertEquals(tree1.children.size(), 1);
        for (FPTree child : tree1.children) {
            assertEquals(child.count, 1);
        }
    }

    @Test
    public void testGetSortedTransaction() {
        SortedSet<String> transaction = new TreeSet<>();
        transaction.add("bread");
        transaction.add("beer");
        transaction.add("milk");

        Map<String, Integer> frequencyMap = new TreeMap<>();
        frequencyMap.put("bread", 2);
        frequencyMap.put("beer", 1);
        frequencyMap.put("milk", 1);

        SortedSet<Item> sortedTransaction = Apriori.getSortedTransaction(transaction, frequencyMap);

        assertEquals(sortedTransaction.first().item, "bread");
        assertEquals(sortedTransaction.last().item, "milk");

        sortedTransaction.remove(sortedTransaction.first());
        assertEquals(sortedTransaction.first().item, "beer");
    }

    @Test
    public void testFindFrequentItemsetsInSinglePrefixPath() {
        FPTree tree =

        Vector<String> actual = FPTree.findFrequentItemsetsInSinglePrefixPath();
    }
}