package store.domain;

import java.util.ArrayList;
import java.util.List;

public class Receipt {
    private static final int NO_QUANTITY = 0;

    private final List<CartItem> purchasedItems;
    private final List<CartItem> freeItems;
    private final Money totalPrice;

    public Receipt(Cart cart) {
        this.purchasedItems = cart.getItems();
        this.freeItems = calculateFreeItems(cart);
        this.totalPrice = cart.getTotalPrice();
    }

    private List<CartItem> calculateFreeItems(Cart cart) {
        List<CartItem> items = cart.getItems();
        return getFreeItemsFromCart(items);
    }

    private List<CartItem> getFreeItemsFromCart(List<CartItem> items) {
        List<CartItem> freeItems = new ArrayList<>();
        for (CartItem item : items) {
            addFreeItems(freeItems, item);
        }
        return freeItems;
    }

    private void addFreeItems(List<CartItem> freeItems, CartItem item) {
        int freeQuantity = item.getFreeQuantity();
        if (freeQuantity > NO_QUANTITY) {
            freeItems.add(item);
        }
    }

    public int getTotalQuantity() {
        return purchasedItems.size();
    }

    public int getTotalPrice() {
        return totalPrice.getAmount();
    }

    public int getPromotionDiscount(Cart cart) {
        return cart.getTotalPromotionDiscount();
    }

    public int getMembershipDiscount(Cart cart, Membership membership) {
        return membership.calculateDiscount(cart.getTotalNonPromoAmount());
    }

    public int getFinalPrice(Cart cart, Membership membership) {
        return totalPrice.getAmount() - getPromotionDiscount(cart) - getMembershipDiscount(cart, membership);
    }

    public List<CartItem> getPurchasedItems() {
        return purchasedItems;
    }

    public List<CartItem> getFreeItems() {
        return freeItems;
    }
}
