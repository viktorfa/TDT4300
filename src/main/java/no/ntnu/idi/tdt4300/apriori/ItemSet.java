package no.ntnu.idi.tdt4300.apriori;

import java.util.Set;
import java.util.TreeSet;

public class ItemSet implements Comparable {
    private Set<String> items;
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

    public void addItem(String item) {
        this.items.add(item);
    }

    public int size() {
        return this.items.size();
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

        return this.items.toString().compareTo(other.items.toString());
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
}
