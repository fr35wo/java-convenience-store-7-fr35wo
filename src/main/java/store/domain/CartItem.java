package store.domain;

import camp.nextstep.edu.missionutils.DateTimes;

public class CartItem {
    private static final int DEFAULT_FREE_QUANTITY = 0;
    private static final int MINIMUM_VALID_QUANTITY = 0;

    private final Product product;
    private final int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Money calculateTotalPrice() {
        return product.calculateTotalPrice(quantity);
    }

    public int calculatePromotionDiscount() {
        if (!product.isPromotionValid()) {
            return 0;
        }
        Money totalAmount = calculateTotalPrice();
        Money amountWithoutPromotion = getTotalAmountWithoutPromotion();
        return totalAmount.subtract(amountWithoutPromotion).getAmount();
    }

    public int calculateTotalIfNoPromotion() {
        if (!product.isPromotionValid()) {
            return calculateTotalPrice().getAmount();
        }
        return 0;
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
        return promotion != null && promotion.isValid(DateTimes.now().toLocalDate());
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
        return (quantity - promoAvailableQuantity) > MINIMUM_VALID_QUANTITY;
    }

    public int getFreeQuantity() {
        Promotion promotion = product.getPromotion();
        if (promotion == null) {
            return DEFAULT_FREE_QUANTITY;
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
        return Math.max(MINIMUM_VALID_QUANTITY, quantity - promoAvailableQuantity);
    }

    public int calculateAdditionalQuantityNeeded() {
        Promotion promotion = product.getPromotion();
        if (!isPromotionValid(promotion)) {
            return DEFAULT_FREE_QUANTITY;
        }
        return calculateAdditionalQuantity(promotion);
    }

    private int calculateAdditionalQuantity(Promotion promotion) {
        int totalRequired = promotion.getBuyQuantity() + promotion.getFreeQuantity();
        int remainder = quantity % totalRequired;
        if (remainder == promotion.getBuyQuantity()) {
            return totalRequired - remainder;
        }
        return DEFAULT_FREE_QUANTITY;
    }

    public CartItem withUpdatedQuantityForFullPrice(int newQuantity) {
        return new CartItem(this.product, newQuantity);
    }

    public CartItem withAdditionalQuantity(int additionalQuantity) {
        return new CartItem(this.product, this.quantity + additionalQuantity);
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
