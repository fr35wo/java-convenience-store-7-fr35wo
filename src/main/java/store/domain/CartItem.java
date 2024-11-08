package store.domain;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;

public class CartItem {
    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Money calculateTotalPrice() {
        return product.getPrice().multiply(quantity);
    }

    public Money getTotalAmountWithoutPromotion() {
        int effectivePaidQuantity = getEffectivePaidQuantity();
        return product.getPrice().multiply(effectivePaidQuantity);
    }

    public int getEffectivePaidQuantity() {
        Promotion promotion = product.getPromotion();
        if (promotion != null && promotion.isValid(LocalDate.now())) {
            int buyQuantity = promotion.getBuyQuantity();
            int freeQuantity = promotion.getFreeQuantity();
            int totalRequired = buyQuantity + freeQuantity;
            int quotient = quantity - Math.min(quantity / totalRequired, product.getStock() / totalRequired);

            return quotient;

        }
        return quantity;
    }

    public boolean isPromotionValid() {
        LocalDate currentDate = DateTimes.now().toLocalDate();
        Promotion promotion = product.getPromotion();
        return promotion != null && promotion.isValid(currentDate);
    }

    public boolean checkPromotionStock() {
        Promotion promotion = product.getPromotion();
        if (promotion != null && promotion.isValid(DateTimes.now().toLocalDate())) {
            int buyQuantity = promotion.getBuyQuantity();
            int freeQuantity = promotion.getFreeQuantity();
            int totalRequired = buyQuantity + freeQuantity;

            int promoAvailableQuantity = (product.getStock() / totalRequired) * totalRequired;
            int remainingQuantity = quantity - promoAvailableQuantity;

            return remainingQuantity > 0;
        }
        return false;
    }

    public int getFreeQuantity() {
        Promotion promotion = product.getPromotion();
        if (promotion != null && promotion.isValid(LocalDate.now())) {
            int buyQuantity = promotion.getBuyQuantity();
            int freeQuantity = promotion.getFreeQuantity();
            int totalRequired = buyQuantity + freeQuantity;
            int promoSets = Math.min(quantity / totalRequired, product.getStock() / totalRequired);

            return promoSets * freeQuantity;
        }
        return 0;
    }

    public int calculateRemainingQuantity() {
        Promotion promotion = product.getPromotion();
        int buyQuantity = promotion.getBuyQuantity();
        int freeQuantity = promotion.getFreeQuantity();
        int totalRequired = buyQuantity + freeQuantity;

        int promoAvailableQuantity = (product.getStock() / totalRequired) * totalRequired;
        return Math.max(0, quantity - promoAvailableQuantity);
    }

    public int calculateAdditionalQuantityNeeded() {
        Promotion promotion = product.getPromotion();
        int buyQuantity = promotion.getBuyQuantity();
        int freeQuantity = promotion.getFreeQuantity();
        int totalRequired = buyQuantity + freeQuantity;
        int remainder = quantity % totalRequired;

        if (remainder == buyQuantity) {
            return totalRequired - remainder;
        }
        return 0;
    }

    public void updateQuantityForFullPrice(int promoAvailableQuantity) {
        this.quantity = promoAvailableQuantity;
    }

    public void updateQuantityForPromotion(int additionalQuantityNeeded) {
        this.quantity += additionalQuantityNeeded;
    }

    public String getProductName() {
        return product.getName();
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    // 프로모션이 적용되는지 여부를 확인하는 메서드 추가
    public boolean hasPromotion() {
        return product.getPromotion() != null && product.getPromotion().isValid(DateTimes.now().toLocalDate());
    }
}