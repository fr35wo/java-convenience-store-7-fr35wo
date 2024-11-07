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
        // 프로모션 재고 차감 시
        if (promoQuantity > 0 && promotion != null) {
            if (promoQuantity > stock) {
                throw new IllegalStateException("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
            }
            stock -= promoQuantity;
        }

        // 일반 재고 차감 시
        if (regularQuantity > 0 && (promotion == null || promoQuantity == 0)) {
            if (regularQuantity > stock) {
                throw new IllegalStateException("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
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

    public void addStock(int additionalStock) {
        this.stock += additionalStock;
    }
}
