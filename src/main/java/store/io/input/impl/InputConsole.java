package store.io.input.impl;

import camp.nextstep.edu.missionutils.Console;
import store.io.input.StoreInput;

public class InputConsole implements StoreInput {

    @Override
    public String getPurchaseItemsInput() {
        System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        return Console.readLine();
    }

    @Override
    public boolean askForAdditionalPromo(String itemName, int additionalCount) {
        System.out.printf("현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\n", itemName, additionalCount);
        return getYesNoResponse();
    }

    @Override
    public boolean askForFullPricePurchase(String itemName, int shortageCount) {
        System.out.printf("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)\n", itemName, shortageCount);
        return getYesNoResponse();
    }

    @Override
    public boolean askForMembershipDiscount() {
        System.out.println("멤버십 할인을 받으시겠습니까? (Y/N)");
        return getYesNoResponse();
    }

    @Override
    public boolean askForAdditionalPurchase() {
        System.out.println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
        return getYesNoResponse();
    }

    private boolean getYesNoResponse() {
        while (true) {
            String input = Console.readLine().trim().toUpperCase();
            if ("Y".equals(input)) {
                return true;
            }
            if ("N".equals(input)) {
                return false;
            }
            System.out.println("잘못된 입력입니다. Y 또는 N을 입력해 주세요.");
        }
    }
}
