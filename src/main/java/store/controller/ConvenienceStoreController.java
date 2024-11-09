package store.controller;

import java.util.List;
import store.domain.Cart;
import store.domain.CartItem;
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
                convenienceStoreService.printInventoryProductList(storeOutput);

                List<ParsedItem> parsedItems = getValidParsedItems();
                List<CartItem> items = getValidCartItems(parsedItems);

                convenienceStoreService.applyPromotionToCartItems(items, storeInput);

                Membership membership = getValidMembershipResponse();
                Receipt receipt = convenienceStoreService.createReceipt(items, membership);
                storeOutput.printReceipt(receipt);

                updateInventory(new Cart(items));

                continueShopping = getValidAdditionalPurchaseResponse();

            } while (continueShopping);

            storeOutput.printThankYouMessage();
        } finally {
            storeOutput.close();
        }
    }

    private List<ParsedItem> getValidParsedItems() {
        while (true) {
            try {
                String input = storeInput.getPurchaseItemsInput();
                return convenienceStoreService.parseItems(input);
            } catch (IllegalArgumentException e) {
                storeOutput.printError(e.getMessage());
            }
        }
    }

    private List<CartItem> getValidCartItems(List<ParsedItem> parsedItems) {
        while (true) {
            try {
                return convenienceStoreService.createCartItems(parsedItems);
            } catch (IllegalArgumentException | IllegalStateException e) {
                storeOutput.printError(e.getMessage());
            }
        }
    }

    private Membership getValidMembershipResponse() {
        while (true) {
            try {
                boolean isMembership = storeInput.askForMembershipDiscount();
                if (isMembership) {
                    return Membership.Y;
                }
                return Membership.N;
            } catch (IllegalArgumentException e) {
                storeOutput.printError(e.getMessage());
            }
        }
    }

    private void updateInventory(Cart cart) {
        while (true) {
            try {
                convenienceStoreService.updateInventory(cart);
                break;
            } catch (IllegalStateException e) {
                storeOutput.printError(e.getMessage());
            }
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
