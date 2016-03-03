package no.ntnu.idi.tdt4300.apriori;


public class Item implements Comparable {
    public String item;
    public Integer frequency;

    public Item(String item, int frequency) {
        this.item = item;
        this.frequency = frequency;
    }

    @Override
    public int compareTo(Object o) {
        Item other = (Item) o;
        if (other.frequency.equals(this.frequency)) return this.item.compareTo(other.item);
        else return other.frequency.compareTo(this.frequency);
    }
}
