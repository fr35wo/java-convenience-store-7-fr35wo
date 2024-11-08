package store.service;

import java.util.List;
import store.domain.CartItem;
import store.domain.Inventory;
import store.domain.PurchaseItemParser;
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

    public List<CartItem> createCartItems(String input) {
        return parser.parse(input, inventory);
    }
}
