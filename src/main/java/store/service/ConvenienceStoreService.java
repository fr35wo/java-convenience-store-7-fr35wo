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

    public Cart createCart(List<ParsedItem> parsedItems) {
        Cart cart = new Cart();
        for (ParsedItem parsedItem : parsedItems) {
            cart.addItem(parsedItem.toCartItem());
        }
        return cart;
    }

    public void applyPromotionToCartItems(Cart cart, StoreInput storeInput) {
        List<CartItem> items = new ArrayList<>(cart.getItems());
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            if (item.isPromotionValid() && !item.checkPromotionStock()) {
                handleAdditionalPromo(item, storeInput, cart, i);
            }

            if (item.isPromotionValid() && item.calculateRemainingQuantity() > 0) {
                handleFullPricePurchase(item, storeInput, cart, i);
            }
        }
    }

    private void handleAdditionalPromo(CartItem item, StoreInput storeInput, Cart cart, int index) {
        int additionalQuantityNeeded = item.calculateAdditionalQuantityNeeded();
        if (additionalQuantityNeeded > 0) {
            boolean addMore = storeInput.askForAdditionalPromo(item.getProductName(), additionalQuantityNeeded);
            if (addMore) {
                CartItem updatedItem = item.withAdditionalQuantity(additionalQuantityNeeded);
                cart.replaceItem(index, updatedItem);
            }
        }
    }

    private void handleFullPricePurchase(CartItem item, StoreInput storeInput, Cart cart, int index) {
        int remainingQuantity = item.calculateRemainingQuantity();
        boolean continueFullPricePurchase = storeInput.askForFullPricePurchase(item.getProductName(),
                remainingQuantity);
        if (!continueFullPricePurchase) {
            CartItem updatedItem = item.withUpdatedQuantityForFullPrice(item.getQuantity() - remainingQuantity);
            cart.replaceItem(index, updatedItem);
        }
    }


    public Membership determineMembership(StoreInput storeInput) {
        boolean isMembership = storeInput.askForMembershipDiscount();
        if (isMembership) {
            return Membership.Y;
        }
        return Membership.N;
    }

    public void updateInventory(Cart cart) {
        inventory.updateInventory(cart);
    }

    public Receipt createReceipt(Cart cart, Membership membership) {
        return new Receipt(cart, membership);
    }
}
