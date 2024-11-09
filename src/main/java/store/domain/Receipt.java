package store.domain;

import java.util.ArrayList;
import java.util.List;

public class Receipt {
    private final List<CartItem> purchasedItems;
    private final List<String> freeItems;
    private final Money totalPrice;
    private final int promotionDiscount;
    private final int membershipDiscount;

    public Receipt(Cart cart, Membership membership) {
        this.purchasedItems = getCartItems(cart);
        this.freeItems = calculateFreeItems(cart);
        this.totalPrice = calculateTotalPrice(cart);
        this.promotionDiscount = calculatePromotionDiscount(cart);
        this.membershipDiscount = membership.calculateMembershipDiscount(calculateNonPromoTotal(cart));
    }

    private List<CartItem> getCartItems(Cart cart) {
        return cart.getItems();
    }

    private Money calculateTotalPrice(Cart cart) {
        List<CartItem> items = getCartItems(cart);
        return calculateItemsTotalPrice(items);
    }

    private Money calculateItemsTotalPrice(List<CartItem> items) {
        Money total = new Money(0);
        for (CartItem item : items) {
            total = total.add(item.calculateTotalPrice());
        }
        return total;
    }

    private List<String> calculateFreeItems(Cart cart) {
        List<CartItem> items = getCartItems(cart);
        return getFreeItemsFromCart(items);
    }

    private List<String> getFreeItemsFromCart(List<CartItem> items) {
        List<String> freeItems = new ArrayList<>();
        for (CartItem item : items) {
            addFreeItems(freeItems, item);
        }
        return freeItems;
    }

    private void addFreeItems(List<String> freeItems, CartItem item) {
        int freeQuantity = item.getFreeQuantity();
        if (freeQuantity > 0) {
            String freeItemDescription = item.getProduct().getName() + " " + freeQuantity;
            freeItems.add(freeItemDescription);
        }
    }

    private int calculatePromotionDiscount(Cart cart) {
        List<CartItem> items = getCartItems(cart);
        return getPromotionDiscountFromItems(items);
    }

    private int getPromotionDiscountFromItems(List<CartItem> items) {
        int discount = 0;
        for (CartItem item : items) {
            discount += calculateItemPromotionDiscount(item);
        }
        return discount;
    }

    private int calculateItemPromotionDiscount(CartItem item) {
        Money totalAmount = item.calculateTotalPrice();
        Money amountWithoutPromotion = item.getTotalAmountWithoutPromotion();
        return totalAmount.subtract(amountWithoutPromotion).getAmount();
    }

    private int calculateNonPromoTotal(Cart cart) {
        List<CartItem> items = getCartItems(cart);
        return getNonPromoTotal(items);
    }

    private int getNonPromoTotal(List<CartItem> items) {
        int total = 0;
        for (CartItem item : items) {
            if (!item.hasPromotion()) {
                total += item.calculateTotalPrice().getAmount();
            }
        }
        return total;
    }

    public List<CartItem> getPurchasedItems() {
        return purchasedItems;
    }

    public List<String> getFreeItems() {
        return freeItems;
    }

    public int getTotalPrice() {
        return totalPrice.getAmount();
    }

    public int getPromotionDiscount() {
        return promotionDiscount;
    }

    public int getMembershipDiscount() {
        return membershipDiscount;
    }

    public int getFinalPrice() {
        return totalPrice.getAmount() - promotionDiscount - membershipDiscount;
    }
}
