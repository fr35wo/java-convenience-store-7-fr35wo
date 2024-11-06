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
        this.totalPrice = calculateTotalPrice(cart); // 전체 수량 기준 총 구매액
        this.promotionDiscount = calculatePromotionDiscount(cart); // 프로모션 할인 계산
        this.membershipDiscount = membership.isMember() ? membership.getDiscount(totalPrice).getAmount() : 0;
        updateInventory(cart, inventory);
    }

    private Money calculateTotalPrice(Cart cart) {
        return cart.getItems().stream()
                .map(CartItem::calculateTotalPrice) // 전체 수량 기준 총 구매액
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
            discount += fullPrice.subtract(effectivePrice).getAmount(); // 전체 수량과 지불 수량의 차액을 할인으로 계산
        }
        return discount;
    }

    private void updateInventory(Cart cart, Inventory inventory) {
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            Promotion promotion = product.getPromotion();

            int requiredQuantity = item.getQuantity();

            if (promotion != null && promotion.isValid(LocalDate.now())) {
                int buyQuantity = promotion.getBuyQuantity();
                int freeQuantity = promotion.getFreeQuantity();
                int totalRequired = buyQuantity + freeQuantity;

                // 프로모션 재고에서 가능한 최대 차감량
                int availablePromoStock = product.getStock();
                int promoQuantity = Math.min(requiredQuantity, availablePromoStock);

                // 남은 수량을 일반 재고에서 차감
                int regularQuantity = requiredQuantity - promoQuantity;

                // 인벤토리에서 프로모션 및 일반 재고 각각 차감
                inventory.reduceStock(product.getName(), promoQuantity, regularQuantity);
            } else {
                // 프로모션이 없을 경우 전체 수량을 일반 재고에서 차감
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
