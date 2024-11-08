package store.service;

import java.util.ArrayList;
import java.util.List;
import store.domain.CartItem;
import store.domain.Inventory;
import store.domain.ParsedItem;
import store.domain.PurchaseItemParser;
import store.io.input.StoreInput;
import store.io.output.StoreOutput;

public class ConvenienceStoreService {
    private final PurchaseItemParser parser;
    private Inventory inventory;

    public ConvenienceStoreService() {
        this.parser = new PurchaseItemParser();
        this.inventory = createInventory(); // Inventory 객체 생성
    }

    public Inventory getInventory() {
        return inventory;
    }

    // Inventory 객체를 생성하는 메서드
    private Inventory createInventory() {
        return new Inventory();
    }

    // Inventory에 있는 상품 목록을 출력하는 메서드
    public void printInventoryProductList(StoreOutput storeOutput) {
        inventory.printProductList(storeOutput);
    }

    // 문자열을 파싱하여 ParsedItem 리스트를 반환하는 메서드
    public List<ParsedItem> parseItems(String input) {
        return parser.parse(input, inventory);
    }

    // ParsedItem 리스트를 사용하여 CartItem 리스트를 생성하는 메서드
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
                // 추가 수량 프로모션 여부 확인
                int additionalQuantityNeeded = item.calculateAdditionalQuantityNeeded();
                if (additionalQuantityNeeded > 0) {
                    boolean addMore = storeInput.askForAdditionalPromo(item.getProductName(), additionalQuantityNeeded);
                    if (addMore) {
                        item.updateQuantityForPromotion(additionalQuantityNeeded);
                    }
                }
            }

            if (item.isPromotionValid() && item.calculateRemainingQuantity() > 0) {
                // 프로모션 할인이 적용되지 않는 수량에 대해 사용자에게 구매 여부 확인
                int remainingQuantity = item.calculateRemainingQuantity();
                boolean continueFullPricePurchase = storeInput.askForFullPricePurchase(item.getProductName(),
                        remainingQuantity);
                if (!continueFullPricePurchase) {
                    item.updateQuantityForFullPrice(item.getQuantity() - remainingQuantity);
                }
            }
        }
    }
}
