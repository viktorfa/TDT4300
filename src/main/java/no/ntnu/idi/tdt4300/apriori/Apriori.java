package no.ntnu.idi.tdt4300.apriori;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is the main class of the association rule generator.
 * <p>
 * It's a dummy reference program demonstrating the accepted command line arguments, input file format and standard output
 * format also required from your implementation. The generated standard output follows the CSV (comma-separated values) format.
 * <p>
 * It's up to you if you use this program as your base, however, it's very important to strictly follow the given formatting
 * of the inputs and outputs. Your assignment will be partly automatically evaluated, therefore keep the input arguments
 * and output format identical.
 * <p>
 * Alright, I believe it's enough to stress three times the importance of the input and output formatting. Four times...
 *
 * @author tdt4300-undass@idi.ntnu.no
 */
public class Apriori {

    /**
     * Loads the transaction from the ARFF file.
     *
     * @param filepath relative path to ARFF file
     * @return list of transactions as sets
     * @throws java.io.IOException signals that I/O error has occurred
     */
    public static List<SortedSet<String>> readTransactionsFromFile(String filepath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        List<String> attributeNames = new ArrayList<String>();
        List<SortedSet<String>> itemSets = new ArrayList<SortedSet<String>>();

        String line = reader.readLine();
        while (line != null) {
            if (line.contains("#") || line.length() < 2) {
                line = reader.readLine();
                continue;
            }
            if (line.contains("attribute")) {
                int startIndex = line.indexOf("'");
                if (startIndex > 0) {
                    int endIndex = line.indexOf("'", startIndex + 1);
                    attributeNames.add(line.substring(startIndex + 1, endIndex));
                }
            } else {
                SortedSet<String> is = new TreeSet<String>();
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                int attributeCounter = 0;
                String itemSet = "";
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken().trim();
                    if (token.equalsIgnoreCase("t")) {
                        String attribute = attributeNames.get(attributeCounter);
                        itemSet += attribute + ",";
                        is.add(attribute);
                    }
                    attributeCounter++;
                }
                itemSets.add(is);
            }
            line = reader.readLine();
        }
        reader.close();

        return itemSets;
    }

    /**
     * Generates the frequent itemsets given the support threshold. The results are returned in CSV format.
     *
     * @param transactions list of transactions
     * @param support      support threshold
     * @return frequent itemsets in CSV format with columns size and items; columns are semicolon-separated and items are comma-separated
     */
    public static String generateFrequentItemsets(List<SortedSet<String>> transactions, double support) {
        int minSupport = getMinSupport(support, transactions.size());


        Map<String, Set<Integer>> occurences = getOccurences(transactions);


        Set<ItemSet> itemSets = getItemSets(transactions, new HashMap<>(), minSupport);

        StringBuilder result = new StringBuilder();
        result.append("size;items\n");


        int setSize = 0;
        for (ItemSet set : itemSets.stream().sorted().collect(Collectors.toList())) {
            setSize = set.size();
            result.append(setSize);
            result.append(';');
            result.append(set);
            result.append('\n');
        }

        return result.toString();
    }

    /**
     * Creates a map from each item in a list of transactions mapped to a set of integers representing the transaction
     * that contains the item by its index in the transactions list.
     * @param transactions
     * @return Mapping between items and the transaction in which they are found. E.g. "eggs" -> [3, 4]
     */
    public static Map<String, Set<Integer>> getOccurences(List<SortedSet<String>> transactions) {
        Map<String, Set<Integer>> result = new HashMap<>();

        int counter = 0;
        for (SortedSet<String> transaction : transactions) {
            for (String item : transaction) {
                if (result.containsKey(item)) {
                    result.get(item).add(counter);
                } else {
                    TreeSet<Integer> transactionSet = new TreeSet<>();
                    transactionSet.add(counter);
                    result.put(item, transactionSet);
                }
            }
            counter++;
        }

        return result;
    }

    /**
     * Calculates the minimum size an item set needs to be frequent.
     * @param support
     * @param size
     * @return
     */
    public static int getMinSupport(double support, int size) {
        return (int) Math.ceil(support * size);
    }

    /**
     * Finds the size of the largest set in a collection of sets.
     * @param list A collection containing sets.
     * @return Integer the size of the largest set in the list.
     */
    public static int sizeOfLargestSet(Collection<SortedSet<String>> list) {
        int result = 0;
        for (SortedSet<String> setItem : list) {
            if (setItem.size() > result) result = setItem.size();
        }
        return result;
    }

    /**
     * Recursively finds frequent item sets in a list of transactions. This is the implementation of the Apriori
     * algorithm.
     * @param transactions
     * @param occurences Mapping from ItemSet to a set of integers representing the transactions in which item sets
     *                   occur. Can be an empty HashMap initally, but is filled when called recursively.
     * @param minSupport Integer representing the minimum number of occurences an item set needs to be counted.
     * @return All item sets that are frequent according to the transactions and the minSupport.
     */
    public static Set<ItemSet> getItemSets(
            List<SortedSet<String>> transactions,
            Map<ItemSet, Set<Integer>> occurences,
            int minSupport) {

        int initialItemSets = occurences.size(); // Used to know when to end recursive call at the end of the function

        Set<ItemSet> result = new TreeSet<>();

        // We create a map of the occurences of single items that are within the support limit.
        if (occurences.isEmpty()) {
            for (Map.Entry<String, Set<Integer>> entry : getOccurences(transactions).entrySet()) {
                if (entry.getValue().size() >= minSupport) {
                    result.add(new ItemSet(entry.getKey()));
                    occurences.put(new ItemSet(entry.getKey()), entry.getValue());
                }
            }
        }
        Set<ItemSet> itemSets = new TreeSet<>(occurences.keySet()); // The item sets we might extend in this iteration
        for (ItemSet itemSet : itemSets) {
            for (Map.Entry<String, Set<Integer>> entry : getOccurences(transactions).entrySet()) {
                if (entry.getValue().size() >= minSupport) {
                    if (isNewItemSet(occurences.get(itemSet), entry.getValue(), minSupport)) {
                        result.add(new ItemSet(itemSet, entry.getKey()));
                        Set<Integer> newOccurences = new TreeSet<>();
                        for (int i : entry.getValue()) {
                            if (occurences.get(itemSet).contains(i)) {
                                newOccurences.add(i);
                            }
                        }
                        occurences.put(new ItemSet(itemSet, entry.getKey()), newOccurences);
                    }
                }
            }
            occurences.remove(itemSet);
        }
        // If any new item sets were added this iteration, we might find new ones in a new iteration.
        if (result.size() > initialItemSets) {
            result.addAll(getItemSets(transactions, occurences, minSupport));
            return result;
        } else return result;
    }

    /**
     * Helper function to getItemSets used to decide whether a union of an item set and a single item will form a new
     * frequent item set.
     * @param itemSetOccurences Set of integers where the item set is found in the transaction list.
     * @param itemOccurences Set of integers where the single item is found in the transaction list.
     * @param minSupport Integer representing the minimum number of occurences needed for the item set to be frequent.
     * @return Boolean of whether the union of the item set and the single item is frequent or not.
     */
    public static boolean isNewItemSet(Set<Integer> itemSetOccurences, Set<Integer> itemOccurences, int minSupport) {
        Set<Integer> intersection = new TreeSet<>(itemOccurences);
        intersection.retainAll(itemSetOccurences);
        if (intersection.size() >= minSupport) return true;
        else return false;
    }

    /**
     * Generates the association rules given the support and confidence threshold. The results are returned in CSV
     * format.
     *
     * @param transactions list of transactions
     * @param support      support threshold
     * @param confidence   confidence threshold
     * @return association rules in CSV format with columns antecedent, consequent, confidence and support; columns are semicolon-separated and items are comma-separated
     */
    public static String generateAssociationRules(List<SortedSet<String>> transactions, double support, double confidence) {
        int minSupport = getMinSupport(support, transactions.size());
        Set<ItemSet> itemSets = getItemSets(transactions, new HashMap<>(), minSupport);

        StringBuilder result = new StringBuilder();
        result.append("antecedent;consequent;confidence;support\n");

        for (ItemSet itemSet : itemSets.stream().sorted().collect(Collectors.toList())) {
            Set<ItemSet> powerSet = powerSet(itemSet);
            powerSet.remove(new ItemSet());
            powerSet.remove(itemSet);


            double itemSetConfidence;
            for (ItemSet subset : powerSet) {
                itemSetConfidence = supportCount(itemSet, subset, transactions);
                ItemSet antecedent = itemSet.intersection(subset);
                ItemSet consequent = itemSet.exclusion(subset);

                result.append(antecedent);
                result.append(';');
                result.append(consequent);
                result.append(';');
                result.append(niceFloatFormat(itemSetConfidence));
                result.append(';');
                result.append(niceFloatFormat(getSupportOfItemSet(itemSet, transactions)));
                result.append('\n');
            }

        }

        return result.toString();
    }

    /**
     * Formats a double to the lowest necessary precision to avoid trailing zeroes.
     * @param d
     * @return
     */
    public static String niceFloatFormat(double d) {
        BigDecimal bd =  new BigDecimal(d).setScale(2, BigDecimal.ROUND_HALF_UP);
        String result = String.format("%s", bd);

        if (result.endsWith("0")) return result.substring(0, result.length() - 1);
        else return result;
    }

    /**
     * Helper function for the generateAssociationRules function used to find the support ratio for two item sets.
     * @param set1 Set whose support count is the numerator.
     * @param set2 Set whose support count is the denominator.
     * @param transactions
     * @return Double between 0 and 1 representing the ratio of occurences between set1 and set2 in the transaction
     * list.
     */
    public static double supportCount(ItemSet set1, ItemSet set2, List<SortedSet<String>> transactions) {
        int set1SupportCount = 0;
        int set2SupportCount = 0;

        for (SortedSet<String> transaction : transactions) {
            if (transaction.containsAll(set1.getItems())) {
                set1SupportCount++;
            }
            if (transaction.containsAll(set2.getItems())) {
                set2SupportCount++;
            }
        }
        if (set2SupportCount == 0) return 0d;

        else return ((double) set1SupportCount) / ((double) set2SupportCount);
    }

    /**
     * Creates a power set (all subsets) of an item set.
     * @param originalSet
     * @return Power set of originalSet.
     */
    public static Set<ItemSet> powerSet(ItemSet originalSet) {
        Set<ItemSet> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new ItemSet());
            return sets;
        }
        List<ItemSet> list = originalSet.toArrayList();
        ItemSet head = list.get(0);
        ItemSet rest = new ItemSet(list.subList(1, list.size()));
        for (ItemSet set : powerSet(rest)) {
            ItemSet newSet = new ItemSet(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    /**
     * Helper function for the generateAssociationRules function used to get the ratio of occurences an item set has
     * in a list of transactions.
     * @param itemSet
     * @param transactions
     * @return Ratio of occurences if item set in transactions.
     */
    public static double getSupportOfItemSet(ItemSet itemSet, List<SortedSet<String>> transactions) {
        int counter = 0;
        for (SortedSet<String> transaction : transactions) {
            if (transaction.containsAll(itemSet.getItems())) counter++;
        }
        return ((double) counter) / ((double) transactions.size());
    }

    /**
     * Main method.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // definition of the accepted command line arguments
        Options options = new Options();
        options.addOption(Option.builder("f").argName("file").desc("input file with transactions").hasArg().required(true).build());
        options.addOption(Option.builder("s").argName("support").desc("support threshold").hasArg().required(true).build());
        options.addOption(Option.builder("c").argName("confidence").desc("confidence threshold").hasArg().required(false).build());
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            // extracting filepath and support threshold from the command line arguments
            String filepath = cmd.getOptionValue("f");
            double support = Double.parseDouble(cmd.getOptionValue("s"));

            // reading transaction from the file
            List<SortedSet<String>> transactions = readTransactionsFromFile(filepath);

            if (cmd.hasOption("c")) {
                // extracting confidence threshold
                double confidence = Double.parseDouble(cmd.getOptionValue("c"));

                // printing generated association rules
                System.out.println(generateAssociationRules(transactions, support, confidence));
            } else {
                // printing generated frequent itemsets
                System.out.println(generateFrequentItemsets(transactions, support));
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.setOptionComparator(null);
            helpFormatter.printHelp("java -jar apriori.jar", options, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
