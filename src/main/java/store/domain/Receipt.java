package store.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Receipt {
    private final List<CartItem> purchasedItems;
    private final List<String> freeItems;
    private final Money totalPrice;
    private final int promotionDiscount;
    private final int membershipDiscount;

    public Receipt(Cart cart, Membership membership, Inventory inventory) {
        this.purchasedItems = cart.getItems();
        this.freeItems = calculateFreeItems(cart);
        this.totalPrice = calculateTotalPrice(cart);
        this.promotionDiscount = calculatePromotionDiscount(cart);
        this.membershipDiscount = calculateMembershipDiscount(cart, membership);
        updateInventory(cart, inventory);
    }

    private Money calculateTotalPrice(Cart cart) {
        return cart.getItems().stream()
                .map(CartItem::calculateTotalPrice)
                .reduce(new Money(0), Money::add);
    }

    private List<String> calculateFreeItems(Cart cart) {
        List<String> freeItems = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            int freeQuantity = item.getFreeQuantity();
            if (freeQuantity > 0) {
                freeItems.add(item.getProduct().getName() + " " + freeQuantity);
            }
        }
        return freeItems;
    }

    private int calculatePromotionDiscount(Cart cart) {
        int discount = 0;
        for (CartItem item : cart.getItems()) {
            Money effectivePrice = item.getTotalAmountWithoutPromotion();
            Money fullPrice = item.calculateTotalPrice();
            discount += fullPrice.subtract(effectivePrice).getAmount();
        }
        return discount;
    }

    // 멤버십 할인을 프로모션이 적용되지 않은 금액에만 적용
    private int calculateMembershipDiscount(Cart cart, Membership membership) {
        if (!membership.isMember()) {
            return 0;
        }

        // 프로모션 미적용 금액 계산
        int nonPromoTotal = cart.getItems().stream()
                .filter(item -> !item.hasPromotion()) // 프로모션이 적용되지 않은 상품만 필터링
                .mapToInt(item -> item.calculateTotalPrice().getAmount())
                .sum();

        // 멤버십 할인 30%, 최대 8,000원 적용
        int discount = (int) Math.min(nonPromoTotal * 0.3, 8000);
        return discount;
    }

    private void updateInventory(Cart cart, Inventory inventory) {
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            Promotion promotion = product.getPromotion();

            int requiredQuantity = item.getQuantity();

            if (promotion != null && promotion.isValid(LocalDate.now())) {
                int availablePromoStock = product.getStock();
                int promoQuantity = Math.min(requiredQuantity, availablePromoStock);

                int regularQuantity = requiredQuantity - promoQuantity;
                inventory.reduceStock(product.getName(), promoQuantity, regularQuantity);
            } else {
                inventory.reduceStock(product.getName(), 0, requiredQuantity);
            }
        }
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