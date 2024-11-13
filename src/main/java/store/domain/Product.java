package store.domain;

import camp.nextstep.edu.missionutils.DateTimes;
import store.common.ErrorMessages;

public class Product {
    private static final String NO_PROMOTION_DESCRIPTION = "";

    private final String name;
    private final Money price;
    private int stock;
    private final Promotion promotion;

    public Product(String name, int price, int stock, Promotion promotion) {
        this.name = name;
        this.price = new Money(price);
        this.stock = stock;
        this.promotion = promotion;
    }

    public Money calculatePrice(int quantity) {
        return price.multiply(quantity);
    }

    public boolean isPromotionValid() {
        return promotion != null && promotion.isValid(DateTimes.now().toLocalDate());
    }

    public void reduceRegularStock(int quantity) {
        if (quantity > stock) {
            throw new IllegalStateException(ErrorMessages.EXCEED_STOCK);
        }
        stock -= quantity;
    }

    public void reducePromotionStock(int promoQuantity) {
        if (promoQuantity > stock) {
            throw new IllegalStateException(ErrorMessages.EXCEED_STOCK);
        }
        stock -= promoQuantity;
    }

    public String getPromotionDescription() {
        if (promotion != null) {
            return promotion.getName();
        }
        return NO_PROMOTION_DESCRIPTION;
    }

    public void addStock(int additionalStock) {
        this.stock += additionalStock;
    }

    public Money calculateTotalPrice(int quantity) {
        return price.multiply(quantity);
    }

    public Money getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getName() {
        return name;
    }

    public Promotion getPromotion() {
        return promotion;
    }
}
