package store.domain;

import java.util.ArrayList;
import java.util.List;

public class Receipt {
    private static final int MINIMUM_AMOUNT = 0;
    private static final int NO_PROMOTION_DISCOUNT = 0;
    private static final int NO_QUANTITY = 0;

    private final List<CartItem> purchasedItems;
    private final List<CartItem> freeItems;
    private final Money totalPrice;
    private final int promotionDiscount;
    private final int membershipDiscount;

    public Receipt(Cart cart, Membership membership) {
        this.purchasedItems = getCartItems(cart);
        this.freeItems = calculateFreeItems(cart);
        this.totalPrice = calculateTotalPrice(cart);
        this.promotionDiscount = calculatePromotionDiscount(cart);
        this.membershipDiscount = membership.calculateDiscount(calculateNonPromoTotal(cart));
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

    private int calculatePromotionDiscount(Cart cart) {
        List<CartItem> items = getCartItems(cart);
        return getPromotionDiscountFromItems(items);
    }

    private int getPromotionDiscountFromItems(List<CartItem> items) {
        int discount = NO_PROMOTION_DISCOUNT;
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
        int total = MINIMUM_AMOUNT;
        for (CartItem item : items) {
            total += getItemTotalIfNonPromo(item);
        }
        return total;
    }

    private int getItemTotalIfNonPromo(CartItem item) {
        if (!item.hasPromotion()) {
            return item.calculateTotalPrice().getAmount();
        }
        return NO_PROMOTION_DISCOUNT;
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
        return promotionDiscount;
    }

    public int getMembershipDiscount() {
        return membershipDiscount;
    }

    public int getFinalPrice() {
        return totalPrice.getAmount() - promotionDiscount - membershipDiscount;
    }
}
