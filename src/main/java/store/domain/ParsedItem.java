package store.domain;

import java.util.List;

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
            throw new IllegalStateException("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }

    public void addToCart(List<CartItem> cartItems) {
        cartItems.add(new CartItem(product, quantity));
    }
}
