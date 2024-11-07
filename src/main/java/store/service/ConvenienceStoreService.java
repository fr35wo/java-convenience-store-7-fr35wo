package store.service;

import java.util.List;
import store.domain.CartItem;
import store.domain.Inventory;
import store.domain.PurchaseItemParser;

public class ConvenienceStoreService {
    private final PurchaseItemParser parser;

    public ConvenienceStoreService() {
        this.parser = new PurchaseItemParser();
    }

    /**
     * 사용자 입력을 받아 CartItem 리스트를 생성.
     *
     * @param input     사용자가 입력한 상품명과 수량 문자열
     * @param inventory 상품 재고 정보
     * @return CartItem 리스트
     */
    public List<CartItem> createCartItems(String input, Inventory inventory) {
        return parser.parse(input, inventory);
    }
}
