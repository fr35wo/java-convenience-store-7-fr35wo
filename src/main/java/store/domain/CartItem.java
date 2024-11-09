package store.domain;

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
        if (isPromotionValid(promotion)) {
            int totalRequired = promotion.getBuyQuantity() + promotion.getFreeQuantity();
            int quotient = quantity - Math.min(quantity / totalRequired, product.getStock() / totalRequired);
            return quotient;
        }
        return quantity;
    }

    public boolean isPromotionValid() {
        Promotion promotion = product.getPromotion();
        return isPromotionValid(promotion);
    }

    private boolean isPromotionValid(Promotion promotion) {
        return promotion != null && promotion.isValid(LocalDate.now());
    }

    public boolean checkPromotionStock() {
        Promotion promotion = product.getPromotion();
        if (isPromotionValid(promotion)) {
            int totalRequired = promotion.getBuyQuantity() + promotion.getFreeQuantity();
            int promoAvailableQuantity = (product.getStock() / totalRequired) * totalRequired;
            int remainingQuantity = quantity - promoAvailableQuantity;
            return remainingQuantity > 0;
        }
        return false;
    }

    public int getFreeQuantity() {
        Promotion promotion = product.getPromotion();
        if (promotion != null) {
            return promotion.calculateFreeItems(quantity, product.getStock());
        }
        return 0;
    }

    public int calculateRemainingQuantity() {
        Promotion promotion = product.getPromotion();
        if (isPromotionValid(promotion)) {
            int totalRequired = promotion.getBuyQuantity() + promotion.getFreeQuantity();
            int promoAvailableQuantity = (product.getStock() / totalRequired) * totalRequired;
            return Math.max(0, quantity - promoAvailableQuantity);
        }
        return quantity;
    }

    public int calculateAdditionalQuantityNeeded() {
        Promotion promotion = product.getPromotion();
        if (isPromotionValid(promotion)) {
            int totalRequired = promotion.getBuyQuantity() + promotion.getFreeQuantity();
            int remainder = quantity % totalRequired;
            if (remainder == promotion.getBuyQuantity()) {
                return totalRequired - remainder;
            }
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

    public boolean hasPromotion() {
        Promotion promotion = product.getPromotion();
        return isPromotionValid(promotion);
    }
}
