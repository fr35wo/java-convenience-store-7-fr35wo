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

        do {
            // 메시지를 전달하여 Inventory가 스스로 상품 목록을 출력하도록 함
            convenienceStoreService.printInventoryProductList(storeOutput);

            // 유효한 ParsedItem 리스트 가져오기 (파싱 단계에서 예외 발생 시 재시도)
            List<ParsedItem> parsedItems = getValidParsedItems();

            // 유효한 CartItem 리스트 생성하기 (객체 생성 단계에서 예외 발생 시 재시도)
            List<CartItem> items = getValidCartItems(parsedItems);

            boolean isMembership = getValidMembershipResponse(); // 잘못된 입력 시 재시도

            Membership membership = new Membership(isMembership);
            Cart cart = new Cart(items);
            Receipt receipt = new Receipt(cart, membership, convenienceStoreService.getInventory());
            storeOutput.printReceipt(receipt);

            continueShopping = getValidAdditionalPurchaseResponse(); // 잘못된 입력 시 재시도

        } while (continueShopping);

        storeOutput.printThankYouMessage();
    }

    // 유효한 ParsedItem 리스트를 가져오는 메서드
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

    // 유효한 CartItem 리스트를 생성하는 메서드
    private List<CartItem> getValidCartItems(List<ParsedItem> parsedItems) {
        while (true) {
            try {
                return convenienceStoreService.createCartItems(parsedItems);
            } catch (IllegalArgumentException | IllegalStateException e) {
                storeOutput.printError(e.getMessage());
            }
        }
    }

    // 유효한 멤버십 할인 여부를 가져오는 메서드
    private boolean getValidMembershipResponse() {
        while (true) {
            try {
                return storeInput.askForMembershipDiscount();
            } catch (IllegalArgumentException e) {
                storeOutput.printError(e.getMessage());
            }
        }
    }

    // 유효한 추가 구매 여부를 가져오는 메서드
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
