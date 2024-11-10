package store.domain;

import java.util.ArrayList;
import java.util.List;

public class Receipt {
    private static final int MINIMUM_AMOUNT = 0;
    private static final int NO_QUANTITY = 0;

    private final List<CartItem> purchasedItems;
    private final List<CartItem> freeItems;
    private final Money totalPrice;

    public Receipt(Cart cart, Membership membership) {
        this.purchasedItems = getCartItems(cart);
        this.freeItems = calculateFreeItems(cart);
        this.totalPrice = calculateTotalPrice(cart);
    }

    private List<CartItem> getCartItems(Cart cart) {
        return cart.getItems();
    }

    private Money calculateTotalPrice(Cart cart) {
        List<CartItem> items = getCartItems(cart);
        return calculateItemsTotalPrice(items);
    }

    private Money calculateItemsTotalPrice(List<CartItem> items) {
        Money total = new Money(MINIMUM_AMOUNT);
        for (CartItem item : items) {
            total = total.add(item.calculateTotalPrice());
        }
        return total;
    }

    private List<CartItem> calculateFreeItems(Cart cart) {
        List<CartItem> items = getCartItems(cart);
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

    private int calculatePromotionDiscount() {
        int discount = 0;
        for (CartItem item : purchasedItems) {
            discount += item.calculatePromotionDiscount();
        }
        return discount;
    }

    private int calculateNonPromoTotal() {
        int total = MINIMUM_AMOUNT;
        for (CartItem item : purchasedItems) {
            total += item.calculateTotalIfNoPromotion();
        }
        return total;
    }

    private int calculateMembershipDiscount(Membership membership) {
        return membership.calculateDiscount(calculateNonPromoTotal());
    }

    public int getTotalQuantity() {
        int totalQuantity = NO_QUANTITY;
        for (CartItem item : purchasedItems) {
            totalQuantity += item.getQuantity();
        }
        return totalQuantity;
    }

    public List<CartItem> getPurchasedItems() {
        return purchasedItems;
    }

    public List<CartItem> getFreeItems() {
        return freeItems;
    }

    public int getTotalPrice() {
        return totalPrice.getAmount();
    }

    public int getPromotionDiscount() {
        return calculatePromotionDiscount();
    }

    public int getMembershipDiscount(Membership membership) {
        return calculateMembershipDiscount(membership);
    }

    public int getFinalPrice(Membership membership) {
        return totalPrice.getAmount() - getPromotionDiscount() - getMembershipDiscount(membership);
    }
}
