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
        return product.calculatePrice(quantity);
    }

    public Money getTotalAmountWithoutPromotion() {
        return product.calculatePrice(getEffectivePaidQuantity());
    }

    public int getEffectivePaidQuantity() {
        Promotion promotion = product.getPromotion();
        if (!isPromotionValid(promotion)) {
            return quantity;
        }
        return calculateQuotient(promotion);
    }

    private int calculateQuotient(Promotion promotion) {
        int totalRequired = promotion.getBuyQuantity() + promotion.getFreeQuantity();
        return quantity - Math.min(quantity / totalRequired, product.getStock() / totalRequired);
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
        if (!isPromotionValid(promotion)) {
            return false;
        }
        return isStockAvailable(promotion);
    }

    private boolean isStockAvailable(Promotion promotion) {
        int totalRequired = promotion.getBuyQuantity() + promotion.getFreeQuantity();
        int promoAvailableQuantity = (product.getStock() / totalRequired) * totalRequired;
        return (quantity - promoAvailableQuantity) > 0;
    }

    public int getFreeQuantity() {
        Promotion promotion = product.getPromotion();
        if (promotion == null) {
            return 0;
        }
        return promotion.calculateFreeItems(quantity, product.getStock());
    }

    public int calculateRemainingQuantity() {
        Promotion promotion = product.getPromotion();
        if (!isPromotionValid(promotion)) {
            return quantity;
        }
        return calculateRemainingQuantityForValidPromotion(promotion);
    }

    private int calculateRemainingQuantityForValidPromotion(Promotion promotion) {
        int totalRequired = promotion.getBuyQuantity() + promotion.getFreeQuantity();
        int promoAvailableQuantity = (product.getStock() / totalRequired) * totalRequired;
        return Math.max(0, quantity - promoAvailableQuantity);
    }

    public int calculateAdditionalQuantityNeeded() {
        Promotion promotion = product.getPromotion();
        if (!isPromotionValid(promotion)) {
            return 0;
        }
        return calculateAdditionalQuantity(promotion);
    }

    private int calculateAdditionalQuantity(Promotion promotion) {
        int totalRequired = promotion.getBuyQuantity() + promotion.getFreeQuantity();
        int remainder = quantity % totalRequired;
        if (remainder == promotion.getBuyQuantity()) {
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

    public boolean hasPromotion() {
        Promotion promotion = product.getPromotion();
        return isPromotionValid(promotion);
    }

    public String getProductName() {
        return product.getName();
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}
