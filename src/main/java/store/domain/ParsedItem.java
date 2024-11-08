package store.domain;

import java.util.List;

public class ParsedItem {
    private final Product product;
    private final int quantity;

    public ParsedItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    public void addToCart(List<CartItem> cartItems) {
        cartItems.add(new CartItem(product, quantity));
    }
}
