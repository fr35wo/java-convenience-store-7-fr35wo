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

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }
}
