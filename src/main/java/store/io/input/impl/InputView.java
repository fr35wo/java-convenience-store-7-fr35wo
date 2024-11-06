package store.io.input.impl;

import camp.nextstep.edu.missionutils.Console;
import java.util.List;
import store.domain.CartItem;
import store.domain.Inventory;
import store.domain.PurchaseItemParser;

public class InputView {

    // 구매할 상품명과 수량을 입력 받음
    public List<CartItem> readPurchaseItems(Inventory inventory) {
        System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        String input = Console.readLine();

        // Use PurchaseItemParser to parse the input and return a list of CartItem
        PurchaseItemParser parser = new PurchaseItemParser();
        return parser.parse(input, inventory);
    }

    // 추가 증정 상품 여부를 묻는 입력 받음
    public boolean askForAdditionalPromo(String itemName, int additionalCount) {
        System.out.printf("현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\n", itemName, additionalCount);
        return getYesNoResponse();
    }

    // 프로모션 재고 부족 시 정가로 결제할지 여부를 묻는 입력 받음
    public boolean askForFullPricePurchase(String itemName, int shortageCount) {
        System.out.printf("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)\n", itemName, shortageCount);
        return getYesNoResponse();
    }

    // 멤버십 할인 적용 여부를 묻는 입력 받음
    public boolean askForMembershipDiscount() {
        System.out.println("멤버십 할인을 받으시겠습니까? (Y/N)");
        return getYesNoResponse();
    }

    // 추가 구매 여부를 묻는 입력 받음
    public boolean askForAdditionalPurchase() {
        System.out.println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
        return getYesNoResponse();
    }

    // Y/N 입력을 Boolean으로 변환하는 메서드
    private boolean getYesNoResponse() {
        while (true) {
            String input = Console.readLine().trim();
            if ("Y".equalsIgnoreCase(input)) {
                return true;
            }
            if ("N".equalsIgnoreCase(input)) {
                return false;
            }
            System.out.println("[ERROR] 잘못된 입력입니다. Y 또는 N을 입력해 주세요.");
        }
    }
}
