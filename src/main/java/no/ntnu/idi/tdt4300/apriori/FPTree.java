package no.ntnu.idi.tdt4300.apriori;

import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class FPTree implements Comparable {
    public String item;
    public int count;
    public Set<FPTree> children;
    public FPTree parent;
    public Set<FPTree> nodeLinks;

    public FPTree() {
        this.item = "";
        this.count = 0;
        this.children = new TreeSet<>();
        this.parent = null;
        this.nodeLinks = null;
    }

    public FPTree(String item, FPTree parent) {
        this.item = item;
        this.count = 1;
        this.children = new TreeSet<>();
        this.parent = parent;
        this.nodeLinks = new TreeSet<>();
    }

    public Set<FPTree> getChildren() {
        return this.children;
    }
    public void addChild(FPTree child) {
        this.children.add(child);
    }

    public void addNodeLink(FPTree node) {
        this.nodeLinks.add(node);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(String.format("{item: %s, count: %d, \n\tchildren: %s, parent: %s}\n",
                this.item, this.count, this.childrenToString(), this.parent == null ? "NONE" : this.parent.item));
        return result.toString();
    }

    private String childrenToString() {
        if (this.children.isEmpty()) return "NONE";
        StringBuilder result = new StringBuilder();
        for (FPTree child : children) {
            result.append(child.toString());
        }
        return result.toString();
    }

    @Override
    public int compareTo(Object o) {
        FPTree other = (FPTree) o;
        return other.item.compareTo(this.item);
    }

    public static ArrayList<FPTree> getProjectionTrees(FPTree root) {
        ArrayList<FPTree> result = new ArrayList<>();
        Queue<FPTree> trees = new PriorityQueue<>();
        trees.add(root);

        int minSupport = 2;

        Map<FPTree, Integer> nodesOfItems = new HashMap<>();

        while (!trees.isEmpty()) {
            FPTree tree = trees.poll();
            trees.addAll(tree.children);

            if (nodesOfItems.containsKey(tree)) nodesOfItems.put(tree, nodesOfItems.get(tree) + 1);
            else nodesOfItems.put(tree, 1);
        }

        Vector<FPTree> singlePrefixPaths = new Vector<>();
        Vector<FPTree> multiplePrefixPaths = new Vector<>();

        for (Map.Entry<FPTree, Integer> entry : nodesOfItems.entrySet()) {
            if (entry.getValue() == 1) {
                singlePrefixPaths.add(entry.getKey());
            } else {
                multiplePrefixPaths.add(entry.getKey());
            }
        }

        for (FPTree path : singlePrefixPaths) {
            findFrequentItemsetsInSinglePrefixPath(path, minSupport, new Vector<String>());
        }



        return result;
    }

    public static Vector<String> findFrequentItemsetsInSinglePrefixPath(FPTree path, int minSupport, Vector<String> result) {
        if (path.count < minSupport) return result;
        else {
            if (result.isEmpty()) result.add("");
            result.add(result.lastElement() + path.item);
        }

        return result;
    }


    public static ArrayList<FPTree> getProjectionRoots(FPTree root) {
        ArrayList<FPTree> result = new ArrayList<>();



        return result;
    }
}

