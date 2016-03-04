package no.ntnu.idi.tdt4300.apriori;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        // TODO: Generate and print frequent itemsets given the method parameters.


        int minSupport = (int) (transactions.size() * support);


        Map<String, Set<Integer>> occurences = getOccurences(transactions);


        Set<ItemSet> itemSets = getItemSets(transactions, new HashMap<>(), minSupport, 100);

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

        /*
        return "size;items\n" +
                "1;beer\n" +
                "1;bread\n" +
                "1;diapers\n" +
                "1;milk\n" +
                "2;beer,diapers\n" +
                "2;bread,diapers\n" +
                "2;bread,milk\n" +
                "2;diapers,milk\n" +
                "3;bread,diapers,milk\n";
        */
    }

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

    public static int sizeOfLargestSet(List<SortedSet<String>> list) {
        int result = 0;
        for (SortedSet<String> setItem : list) {
            if (setItem.size() > result) result = setItem.size();
        }
        return result;
    }

    public static Set<ItemSet> getItemSets(
            List<SortedSet<String>> transactions,
            Map<ItemSet, Set<Integer>> occurences,
            int minSupport,
            int maxSize) {

        int initialItemSets = occurences.size();

        if (maxSize < 1) maxSize = 1;
        int currentMaxItemSetSize = 0;
        for (ItemSet set : occurences.keySet()) {
            if (set.size() > currentMaxItemSetSize) currentMaxItemSetSize = set.size();
        }
        if (currentMaxItemSetSize >= maxSize) {
            return occurences.keySet();
        }
        Set<ItemSet> result = new TreeSet<>();

        if (occurences.isEmpty()) {
            for (Map.Entry<String, Set<Integer>> entry : getOccurences(transactions).entrySet()) {
                if (entry.getValue().size() >= minSupport) {
                    result.add(new ItemSet(entry.getKey()));
                    occurences.put(new ItemSet(entry.getKey()), entry.getValue());
                }
            }
        }
        Set<ItemSet> itemSets = new TreeSet<>(occurences.keySet());
        for (ItemSet itemSet : itemSets) {
            for (Map.Entry<String, Set<Integer>> entry : getOccurences(transactions).entrySet()) {
                if (entry.getValue().size() >= minSupport) {
                    if (itemSet.size() > 1) {
                    }
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
        if (result.size() > initialItemSets) {
            result.addAll(getItemSets(transactions, occurences, minSupport, maxSize));
            return result;
        } else return result;
    }

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
        // TODO: Generate and print association rules given the method parameters.

        int minSupport = (int) (transactions.size() * support);
        Set<ItemSet> itemSets = getItemSets(transactions, new HashMap<>(), minSupport, 100);

        StringBuilder result = new StringBuilder();
        result.append("antecedent;consequent;confidence;support\n");

        for (ItemSet itemSet : itemSets.stream().sorted().collect(Collectors.toList())) {
            Set<ItemSet> powerSet = powerSet(itemSet);
            powerSet.remove(new ItemSet());
            powerSet.remove(itemSet);


            double itemSetConfidence = 0d;
            for (ItemSet subset : powerSet) {
                itemSetConfidence = supportCount(itemSet, subset, transactions);
                ItemSet antecedent = itemSet.intersection(subset);
                ItemSet consequent = itemSet.exclusion(subset);

                result.append(antecedent);
                result.append(';');
                result.append(consequent);
                result.append(';');
                result.append(String.format("%.2f", itemSetConfidence));
                result.append(';');
                result.append(String.format("%.2f", getSupportOfItemSet(itemSet, transactions)));
                result.append('\n');
            }

        }

        return result.toString();

        /*
        return "antecedent;consequent;confidence;support\n" +
                "diapers;beer;0.6;0.5\n" +
                "beer;diapers;1.0;0.5\n" +
                "diapers;bread;0.8;0.67\n" +
                "bread;diapers;0.8;0.67\n" +
                "milk;bread;0.8;0.67\n" +
                "bread;milk;0.8;0.67\n" +
                "milk;diapers;0.8;0.67\n" +
                "diapers;milk;0.8;0.67\n" +
                "diapers,milk;bread;0.75;0.5\n" +
                "bread,milk;diapers;0.75;0.5\n" +
                "bread,diapers;milk;0.75;0.5\n" +
                "bread;diapers,milk;0.6;0.5\n" +
                "milk;bread,diapers;0.6;0.5\n" +
                "diapers;bread,milk;0.6;0.5\n";
        */
    }

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
