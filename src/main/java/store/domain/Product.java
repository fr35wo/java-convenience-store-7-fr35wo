package store.domain;

import store.common.ErrorMessages;

public class Product {
    private final String name;
    private final Money price;
    private int stock;
    private Promotion promotion;

    public Product(String name, int price, int stock, Promotion promotion) {
        this.name = name;
        this.price = new Money(price);
        this.stock = stock;
        this.promotion = promotion;
    }

    public String getName() {
        return name;
    }

    public Promotion getPromotion() {
        return promotion;
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
        return promotion != null ? promotion.getName() : "";
    }

    public void addStock(int additionalStock) {
        this.stock += additionalStock;
    }

    public Money getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public Money calculatePrice(int quantity) {
        return price.multiply(quantity);
    }
}
