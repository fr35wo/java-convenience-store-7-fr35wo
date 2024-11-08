package store.controller;

import java.util.List;
import store.domain.Cart;
import store.domain.CartItem;
import store.domain.Membership;
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

        do {
            // 메시지를 전달하여 Inventory가 스스로 상품 목록을 출력하도록 함
            convenienceStoreService.printInventoryProductList(storeOutput);

            List<CartItem> items = getValidCartItems(); // 잘못된 입력 시 재시도
            boolean isMembership = getValidMembershipResponse(); // 잘못된 입력 시 재시도

            Membership membership = new Membership(isMembership);
            Cart cart = new Cart(items);
            Receipt receipt = new Receipt(cart, membership, convenienceStoreService.getInventory());
            storeOutput.printReceipt(receipt);

            continueShopping = getValidAdditionalPurchaseResponse(); // 잘못된 입력 시 재시도

        } while (continueShopping);

        storeOutput.printThankYouMessage();
    }

    private List<CartItem> getValidCartItems() {
        while (true) {
            try {
                String input = storeInput.getPurchaseItemsInput();
                return convenienceStoreService.createCartItems(input);
            } catch (IllegalArgumentException | IllegalStateException e) {
                storeOutput.printError(e.getMessage());
            }
        }
    }

    private boolean getValidMembershipResponse() {
        while (true) {
            try {
                return storeInput.askForMembershipDiscount();
            } catch (IllegalArgumentException e) {
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
