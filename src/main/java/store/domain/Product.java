package store.domain;

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

    public Money getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void reduceStock(int promoQuantity, int regularQuantity) {
        if (promoQuantity > 0 && promotion != null) {
            if (promoQuantity > stock) {
                throw new IllegalStateException("[ERROR] 프로모션 재고가 부족합니다.");
            }
            stock -= promoQuantity;
        }

        if (regularQuantity > 0 && (promotion == null || promoQuantity == 0)) {
            if (regularQuantity > stock) {
                throw new IllegalStateException("[ERROR] 일반 재고가 부족합니다.");
            }
            stock -= regularQuantity;
        }
    }

    /**
     * 프로모션이 만료되면 프로모션 재고를 일반 재고에 통합하고, 프로모션 정보 제거.
     */
    public void transferPromoStockToRegular() {
        if (promotion != null) {
            promotion = null; // 프로모션 정보 제거
        }
    }

    public String getPromotionDescription() {
        return promotion != null ? promotion.getName() : "";
    }
}
