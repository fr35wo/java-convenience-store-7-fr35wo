package store.service;

import java.util.ArrayList;
import java.util.List;
import store.domain.Cart;
import store.domain.CartItem;
import store.domain.Inventory;
import store.domain.Membership;
import store.domain.ParsedItem;
import store.domain.PurchaseItemParser;
import store.domain.Receipt;
import store.io.input.StoreInput;
import store.io.output.StoreOutput;

public class ConvenienceStoreService {
    private final PurchaseItemParser parser;
    private final Inventory inventory;

    public ConvenienceStoreService() {
        this.parser = new PurchaseItemParser();
        this.inventory = new Inventory();
    }

    public void printInventoryProductList(StoreOutput storeOutput) {
        inventory.printProductList(storeOutput);
    }

    public List<ParsedItem> parseItems(String input) {
        return parser.parse(input, inventory);
    }

    public List<CartItem> createCartItems(List<ParsedItem> parsedItems) {
        List<CartItem> cartItems = new ArrayList<>();
        for (ParsedItem parsedItem : parsedItems) {
            parsedItem.addToCart(cartItems);
        }
        return cartItems;
    }

    public void applyPromotionToCartItems(List<CartItem> items, StoreInput storeInput) {
        for (CartItem item : items) {
            if (item.isPromotionValid() && !item.checkPromotionStock()) {
                handleAdditionalPromo(item, storeInput);
            }

            if (item.isPromotionValid() && item.calculateRemainingQuantity() > 0) {
                handleFullPricePurchase(item, storeInput);
            }
        }
    }

    private void handleAdditionalPromo(CartItem item, StoreInput storeInput) {
        int additionalQuantityNeeded = item.calculateAdditionalQuantityNeeded();
        if (additionalQuantityNeeded > 0) {
            boolean addMore = storeInput.askForAdditionalPromo(item.getProductName(), additionalQuantityNeeded);
            if (addMore) {
                item.updateQuantityForPromotion(additionalQuantityNeeded);
            }
        }
    }

    private void handleFullPricePurchase(CartItem item, StoreInput storeInput) {
        int remainingQuantity = item.calculateRemainingQuantity();
        boolean continueFullPricePurchase = storeInput.askForFullPricePurchase(item.getProductName(),
                remainingQuantity);
        if (!continueFullPricePurchase) {
            item.updateQuantityForFullPrice(item.getQuantity() - remainingQuantity);
        }
    }

    public void updateInventory(Cart cart) {
        inventory.updateInventory(cart);
    }

    public Receipt createReceipt(List<CartItem> items, Membership membership) {
        Cart cart = new Cart(items);
        return new Receipt(cart, membership);
    }
}
