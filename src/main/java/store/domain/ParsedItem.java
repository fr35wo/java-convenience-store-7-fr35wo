package store.domain;

public class ParsedItem {
    private final Product product;
    private final int quantity;

    public ParsedItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}
