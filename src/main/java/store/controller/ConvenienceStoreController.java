package store.controller;

import java.util.List;
import store.domain.Cart;
import store.domain.CartItem;
import store.domain.Inventory;
import store.domain.Membership;
import store.domain.Receipt;
import store.io.input.impl.InputView;
import store.io.output.impl.OutputView;
import store.service.ConvenienceStoreService;

public class ConvenienceStoreController {
    private final InputView inputView;
    private final OutputView outputView;
    private final Inventory inventory;
    private final ConvenienceStoreService convenienceStoreService;

    public ConvenienceStoreController(InputView inputView, OutputView outputView, Inventory inventory,
                                      ConvenienceStoreService convenienceStoreService) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.inventory = inventory;
        this.convenienceStoreService = convenienceStoreService;
    }

    public void start() {
        boolean continueShopping;

        do {
            outputView.printProductList(inventory.getProducts());

            List<CartItem> items = getValidCartItems(); // 잘못된 입력 시 재시도
            boolean isMembership = getValidMembershipResponse(); // 잘못된 입력 시 재시도

            Membership membership = new Membership(isMembership);
            Cart cart = new Cart(items);
            Receipt receipt = new Receipt(cart, membership, inventory);
            outputView.printReceipt(receipt);

            continueShopping = getValidAdditionalPurchaseResponse(); // 잘못된 입력 시 재시도

        } while (continueShopping);

        outputView.printThankYouMessage();
    }

    private List<CartItem> getValidCartItems() {
        while (true) {
            try {
                String input = inputView.getPurchaseItemsInput();
                return convenienceStoreService.createCartItems(input, inventory);
            } catch (IllegalArgumentException | IllegalStateException e) {
                outputView.printError(e.getMessage());
            }
        }
    }

    private boolean getValidMembershipResponse() {
        while (true) {
            try {
                return inputView.askForMembershipDiscount();
            } catch (IllegalArgumentException e) {
                outputView.printError(e.getMessage());
            }
        }
    }

    private boolean getValidAdditionalPurchaseResponse() {
        while (true) {
            try {
                return inputView.askForAdditionalPurchase();
            } catch (IllegalArgumentException e) {
                outputView.printError(e.getMessage());
            }
        }
    }
}
