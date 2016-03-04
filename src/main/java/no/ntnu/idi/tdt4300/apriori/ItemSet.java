package no.ntnu.idi.tdt4300.apriori;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemSet implements Comparable {
    private SortedSet<String> items;
    private int count;

    public ItemSet() {
        items = new TreeSet<>();
        count = 0;
    }

    public ItemSet(String item) {
        items = new TreeSet<>();
        items.add(item);
        count = 0;
    }

    public ItemSet(ItemSet old, String item) {
        items = new TreeSet<>();
        for (String oldItem : old.items) {
            this.items.add(oldItem);
        }
        items.add(item);
        count = 0;
    }

    public ItemSet(ItemSet old) {
        items = new TreeSet<>();
        for (String oldItem : old.items) {
            this.items.add(oldItem);
        }
        count = 0;
    }

    public ItemSet(List<ItemSet> sets) {
        items = new TreeSet<>();
        for (ItemSet itemSet : sets) {
            this.items.addAll(itemSet.items);
        }
        count = 0;
    }
    public ItemSet(Set<String> items) {
        this.items = new TreeSet<>();
        this.items.addAll(items);
        count = 0;
    }


    public void addAll(ItemSet set) {
        for (String item : set.items) {
            this.addItem(item);
        }
    }

    public void addItem(String item) {
        this.items.add(item);
    }

    public int size() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

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


    @Override
    public int compareTo(Object o) {
        ItemSet other = (ItemSet) o;

        if (this.size() == other.size()) {
            return this.items.toString().compareTo(other.items.toString());
        } else {
            return this.size() - other.size();
        }

    }

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

    public boolean contains(ItemSet other) {
        return this.items.containsAll(other.items);
    }

    public boolean contains(Set<String> other) {
        return this.items.containsAll(other);
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
