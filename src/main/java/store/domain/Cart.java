package store.domain;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public void addItem(CartItem item) {
        this.items.add(item);
    }

    public void replaceItem(int index, CartItem newItem) {
        this.items.set(index, newItem);
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }
}
