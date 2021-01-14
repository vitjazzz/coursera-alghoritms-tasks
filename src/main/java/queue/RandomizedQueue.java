package queue;

import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] items;
    private int nextIndex;

    public RandomizedQueue() {
        this.items = (Item[]) new Object[1];
    }

    public boolean isEmpty() {
        return nextIndex == 0;
    }

    public int size() {
        return nextIndex;
    }

    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException();

        if (size() == items.length) {
            resize(items.length * 2);
        }
        items[nextIndex++] = item;
    }

    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException();
        int randomIndex = StdRandom.uniform(nextIndex);
        Item item = items[randomIndex];
        items[randomIndex] = items[nextIndex - 1];
        items[nextIndex - 1] = null;
        nextIndex--;
        return item;
    }

    private void resize(int newSize) {
        Item[] oldArr = items;
        items = (Item[]) new Object[newSize];
        System.arraycopy(oldArr, 0, items, 0, nextIndex);
    }

    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException();
        int randomIndex = StdRandom.uniform(nextIndex);
        return items[randomIndex];
    }

    public Iterator<Item> iterator() {
        return new RandomizedIterator();
    }

    private RandomizedQueue<Item> copy() {
        RandomizedQueue<Item> copy = new RandomizedQueue<>();
        copy.items = (Item[]) new Object[this.nextIndex];
        copy.nextIndex = this.nextIndex;
        System.arraycopy(this.items, 0, copy.items, 0, nextIndex);
        return copy;
    }

    private class RandomizedIterator implements Iterator<Item> {

        private Item[] randomizedItems;
        private int N;

        public RandomizedIterator() {
            RandomizedQueue<Item> queueCopy = copy();
            this.randomizedItems = (Item[]) new Object[queueCopy.nextIndex];
            int i = 0;
            while (!queueCopy.isEmpty()) {
                this.randomizedItems[i++] = queueCopy.dequeue();
            }
        }

        @Override
        public boolean hasNext() {
            return N < randomizedItems.length;
        }

        @Override
        public Item next() {
            return randomizedItems[N++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Unsupported");
        }
    }

    public static void main(String[] args) {
        RandomizedQueue<String> randomizedQueue = new RandomizedQueue<>();
        System.out.println("After construction. Is queue empty - " + randomizedQueue.isEmpty());
        System.out.println("After construction. Queue size - " + randomizedQueue.size());
        randomizedQueue.enqueue("1");
        randomizedQueue.enqueue("2");
        randomizedQueue.enqueue("3");
        randomizedQueue.enqueue("4");
        randomizedQueue.enqueue("5");
        randomizedQueue.enqueue("6");
        randomizedQueue.enqueue("7");
        System.out.println("After adding 7 elements. Is queue empty - " + randomizedQueue.isEmpty());
        System.out.println("After adding 7 elements. Queue size - " + randomizedQueue.size());
        System.out.println("iterator1: ");
        for (String item : randomizedQueue) {
            System.out.print(item + " ");
        }
        System.out.println("\niterator2: ");
        for (String item : randomizedQueue) {
            System.out.print(item + " ");
        }
        System.out.println("\nDequeue - " + randomizedQueue.dequeue());
        System.out.println("Dequeue - " + randomizedQueue.dequeue());
        System.out.println("Sample - " + randomizedQueue.sample());
        System.out.println("Sample - " + randomizedQueue.sample());
        System.out.println("After removing 2 items: ");
        for (String item : randomizedQueue) {
            System.out.print(item + " ");
        }
    }

}