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

    public Money getTotalPrice() {
        Money totalPrice = new Money(0);
        for (CartItem item : items) {
            totalPrice = totalPrice.add(item.calculateTotalPrice());
        }
        return totalPrice;
    }

    public int getTotalPromotionDiscount() {
        int totalDiscount = 0;
        for (CartItem item : items) {
            totalDiscount += item.calculatePromotionDiscount();
        }
        return totalDiscount;
    }

    public int getTotalNonPromoAmount() {
        int total = 0;
        for (CartItem item : items) {
            total += item.calculateTotalIfNoPromotion();
        }
        return total;
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }
}
