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

    public Promotion getPromotion() {
        return promotion;
    }

    public void reduceRegularStock(int quantity) {
        if (quantity > stock) {
            throw new IllegalStateException("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
        stock -= quantity;
    }

    public void reducePromotionStock(int promoQuantity) {
        if (promoQuantity > stock) {
            throw new IllegalStateException("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
        stock -= promoQuantity;
    }

    public String getPromotionDescription() {
        return promotion != null ? promotion.getName() : "";
    }

    public void addStock(int additionalStock) {
        this.stock += additionalStock;
    }

    public void printProductInfo() {
        String stockInfo = stock > 0 ? stock + "개" : "재고 없음";
        String promoInfo = getPromotionDescription();
        System.out.printf("- %s %d원 %s %s\n", name, price.getAmount(), stockInfo, promoInfo);
    }

    public int getStock() {
        return stock;
    }

    public Money calculatePrice(int quantity) {
        return price.multiply(quantity);
    }
}