package queue;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private Node first;
    private Node last;
    private int size;

    public Deque() {

    }

    public boolean isEmpty() {
        return first == null;
    }

    public int size() {
        return size;
    }

    public void addFirst(Item item) {
        validateItem(item);
        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;
        if (oldFirst == null) {
            last = first;
        } else {
            oldFirst.prev = first;
        }
        size++;
    }


    public void addLast(Item item) {
        validateItem(item);
        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.prev = oldLast;
        if (oldLast == null) {
            first = last;
        } else {
            oldLast.next = last;
        }
        size++;
    }

    public Item removeFirst() {
        validateNotEmpty();
        Node oldFirst = first;
        first = first.next;
        if (first == null) {
            last = null;
        } else {
            first.prev = null;
        }
        size--;
        return oldFirst.item;
    }

    public Item removeLast() {
        validateNotEmpty();
        Node oldLast = last;
        last = last.prev;
        if (last == null) {
            first = null;
        } else {
            last.next = null;
        }
        size--;
        return oldLast.item;
    }

    private void validateItem(Item item) {
        if (item == null) throw new IllegalArgumentException("Item cannot be null.");
    }

    private void validateNotEmpty() {
        if (isEmpty()) throw new NoSuchElementException("coursera.queue.Deque is empty.");
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        Node current = first;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Item next() {
            if (current == null) {
                throw new NoSuchElementException("No such element.");
            }
            Item item = current.item;
            current = current.next;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove is unsupported.");
        }
    }

    private class Node {
        Item item;
        Node next;
        Node prev;
    }

    public static void main(String[] args) {
        Deque<String> deque = new Deque<>();
        System.out.println("After construction. Is coursera.queue.Deque empty - " + deque.isEmpty());
        deque.addLast("2");
        deque.addFirst("1");
        deque.addLast("3");
        deque.addLast("4");
        System.out.println("After adding 4 elements. Is coursera.queue.Deque empty - " + deque.isEmpty());
        System.out.println("After adding 4 elements. coursera.queue.Deque size - " + deque.size());
        System.out.println("Items in deque: ");
        for (String item : deque) {
            System.out.print(item + " ");
        }
        System.out.println("Remove last - " + deque.removeLast());
        System.out.println("Items in deque after removing last: ");
        for (String item : deque) {
            System.out.print(item + " ");
        }
        System.out.println("Remove first - " + deque.removeFirst());
        System.out.println("Items in deque after removing first: ");
        for (String item : deque) {
            System.out.print(item + " ");
        }
    }
}
