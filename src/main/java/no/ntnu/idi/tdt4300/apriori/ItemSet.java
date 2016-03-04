package no.ntnu.idi.tdt4300.apriori;

import java.util.*;

/**
 * Class used to represent and item set. Behaves much like a regular set, but has some special constructors and other
 * helpful to make this task easier.
 */
public class ItemSet implements Comparable {
    // Set of the items this item set contains.
    private SortedSet<String> items;

    /*
    Many constructors that are hopefully not too hard to understand without documentation.
     */

    public ItemSet() {
        items = new TreeSet<>();
    }

    public ItemSet(String item) {
        items = new TreeSet<>();
        items.add(item);
    }

    public ItemSet(ItemSet old, String item) {
        items = new TreeSet<>();
        for (String oldItem : old.items) {
            this.items.add(oldItem);
        }
        items.add(item);
    }

    public ItemSet(ItemSet old) {
        items = new TreeSet<>();
        for (String oldItem : old.items) {
            this.items.add(oldItem);
        }
    }

    public ItemSet(List<ItemSet> sets) {
        items = new TreeSet<>();
        for (ItemSet itemSet : sets) {
            this.items.addAll(itemSet.items);
        }
    }

    public ItemSet(Set<String> items) {
        this.items = new TreeSet<>();
        this.items.addAll(items);
    }

    /*
    End constructors.
     */

    /**
     * Adds all items from another item set to this.
     *
     * @param set
     */
    public void addAll(ItemSet set) {
        for (String item : set.items) {
            this.addItem(item);
        }
    }

    /**
     * Adds one item to this.
     *
     * @param item
     */
    public void addItem(String item) {
        this.items.add(item);
    }

    public int size() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    /**
     * Creates an ArrayList of item sets - one for each item in this.
     * @return
     */
    public ArrayList<ItemSet> toArrayList() {
        ArrayList<ItemSet> result = new ArrayList<>();
        for (String item : this.items) {
            result.add(new ItemSet(item));
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != ItemSet.class) {
            return false;
        } else {
            return this.items.equals(((ItemSet) other).items);
        }
    }

    @Override
    public int hashCode() {
        StringBuilder itemString = new StringBuilder();
        for (String item : this.items) {
            itemString.append(item);
        }
        return itemString.toString().hashCode();
    }


    /**
     * Sorts by size ascending and then lexically.
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        ItemSet other = (ItemSet) o;

        if (this.size() == other.size()) {
            return this.items.toString().compareTo(other.items.toString());
        } else {
            return this.size() - other.size();
        }

    }

    /**
     * String in csv format for a single cell.
     * @return
     */
    @Override
    public String toString() {
        if (this.size() > 0) {

            StringBuilder itemString = new StringBuilder();
            for (String item : this.items) {
                itemString.append(item);
                itemString.append(",");
            }
            itemString.deleteCharAt(itemString.length() - 1);
            return itemString.toString();
        } else return "{empty item set}";
    }

    public SortedSet<String> getItems() {
        return this.items;
    }

    public ItemSet intersection(ItemSet other) {
        SortedSet<String> resultItems = new TreeSet<>(this.items);
        resultItems.retainAll(other.items);

        return new ItemSet(resultItems);
    }

    public ItemSet exclusion(ItemSet other) {
        SortedSet<String> resultItems = new TreeSet<>(this.items);
        resultItems.removeAll(other.items);

        return new ItemSet(resultItems);
    }

    public void clear() {
        this.items.clear();
    }
}
