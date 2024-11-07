package store.controller;

import java.util.List;
import store.domain.Cart;
import store.domain.CartItem;
import store.domain.Inventory;
import store.domain.Membership;
import store.domain.Receipt;
import store.io.input.impl.InputView;
import store.io.output.impl.OutputView;

public class ConvenienceStoreController {
    private final InputView inputView;
    private final OutputView outputView;
    private final Inventory inventory;

    public ConvenienceStoreController(InputView inputView, OutputView outputView, Inventory inventory) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.inventory = inventory;
    }

    public void start() {
        boolean continueShopping;

        do {
            try {
                outputView.printProductList(inventory.getProducts());
                List<CartItem> items = inputView.readPurchaseItems(inventory);

                boolean isMembership = inputView.askForMembershipDiscount();
                Membership membership = new Membership(isMembership);

                Cart cart = new Cart(items);
                Receipt receipt = new Receipt(cart, membership, inventory);
                outputView.printReceipt(receipt);

                continueShopping = inputView.askForAdditionalPurchase();

            } catch (IllegalArgumentException | IllegalStateException e) {
                outputView.printError(e.getMessage());
                continueShopping = true; // 재시도하게 설정
            }
        } while (continueShopping);

        outputView.printThankYouMessage();
    }
}
