package store.controller;

import java.util.List;
import store.domain.Cart;
import store.domain.Membership;
import store.domain.ParsedItem;
import store.domain.Receipt;
import store.io.input.StoreInput;
import store.io.output.StoreOutput;
import store.service.ConvenienceStoreService;

public class ConvenienceStoreController {
    private final StoreInput storeInput;
    private final StoreOutput storeOutput;
    private final ConvenienceStoreService convenienceStoreService;

    public ConvenienceStoreController(StoreInput storeInput, StoreOutput storeOutput,
                                      ConvenienceStoreService convenienceStoreService) {
        this.storeInput = storeInput;
        this.storeOutput = storeOutput;
        this.convenienceStoreService = convenienceStoreService;
    }

    public void start() {
        boolean continueShopping;
        try {
            do {
                executeShoppingCycle();
                continueShopping = getValidAdditionalPurchaseResponse();
            } while (continueShopping);
        } finally {
            storeOutput.close();
        }
    }

    private void executeShoppingCycle() {
        convenienceStoreService.printInventoryProductList(storeOutput);

        List<ParsedItem> parsedItems = getValidParsedItems();
        Cart cart = convenienceStoreService.createCart(parsedItems);

        convenienceStoreService.applyPromotionToCartItems(cart, storeInput);

        Membership membership = getValidMembershipResponse();

        Receipt receipt = convenienceStoreService.createReceipt(cart);
        storeOutput.printReceipt(receipt, cart, membership);

        updateInventory(cart);
    }

    private List<ParsedItem> getValidParsedItems() {
        while (true) {
            try {
                String input = storeInput.getPurchaseItemsInput();
                return convenienceStoreService.parseItems(input);
            } catch (IllegalArgumentException | IllegalStateException e) {
                storeOutput.printError(e.getMessage());
            }
        }
    }

    private Membership getValidMembershipResponse() {
        while (true) {
            try {
                return convenienceStoreService.determineMembership(storeInput);
            } catch (IllegalArgumentException e) {
                storeOutput.printError(e.getMessage());
            }
        }
    }

    private void updateInventory(Cart cart) {
        try {
            convenienceStoreService.updateInventory(cart);
        } catch (IllegalStateException e) {
            storeOutput.printError(e.getMessage());
        }
    }

    private boolean getValidAdditionalPurchaseResponse() {
        while (true) {
            try {
                return storeInput.askForAdditionalPurchase();
            } catch (IllegalArgumentException e) {
                storeOutput.printError(e.getMessage());
            }
        }
    }
}
