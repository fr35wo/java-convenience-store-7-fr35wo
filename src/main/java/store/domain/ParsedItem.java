package store.domain;

import java.util.List;
import store.common.ErrorMessages;

public class ParsedItem {
    private final Product product;
    private final int quantity;

    public ParsedItem(Product product, int quantity, Inventory inventory) {
        this.product = product;
        this.quantity = quantity;

        Product promoProduct = getPromoProduct(product.getName(), inventory);
        Product regularProduct = getRegularProduct(product.getName(), inventory);

        int totalAvailableStock = calculateTotalAvailableStock(promoProduct, regularProduct);

        validateStockAvailability(quantity, totalAvailableStock);
    }

    private Product getPromoProduct(String productName, Inventory inventory) {
        return inventory.getProductByNameAndPromotion(productName, true).orElse(null);
    }

    private Product getRegularProduct(String productName, Inventory inventory) {
        return inventory.getProductByNameAndPromotion(productName, false).orElse(null);
    }

    private int calculateTotalAvailableStock(Product promoProduct, Product regularProduct) {
        int promoStock = 0;
        if (promoProduct != null) {
            promoStock = promoProduct.getStock();
        }

        int regularStock = 0;
        if (regularProduct != null) {
            regularStock = regularProduct.getStock();
        }

        return promoStock + regularStock;
    }

    private void validateStockAvailability(int requiredQuantity, int totalAvailableStock) {
        if (requiredQuantity > totalAvailableStock) {
            throw new IllegalStateException(ErrorMessages.EXCEED_STOCK);
        }
    }

    public void addToCart(List<CartItem> cartItems) {
        cartItems.add(new CartItem(product, quantity));
    }
}
