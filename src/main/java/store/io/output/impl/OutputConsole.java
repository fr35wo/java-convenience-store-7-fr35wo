package store.io.output.impl;

import camp.nextstep.edu.missionutils.Console;
import java.util.List;
import store.domain.CartItem;
import store.domain.Product;
import store.domain.Receipt;
import store.io.output.StoreOutput;

public class OutputConsole implements StoreOutput {

    @Override
    public void printProductList(List<Product> products) {
        System.out.println("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.");
        for (Product product : products) {
            product.printProductInfo();
        }
    }

    @Override
    public void printReceipt(Receipt receipt) {
        System.out.println("===========W 편의점=============");
        System.out.println("상품명\t\t수량\t금액");

        for (CartItem item : receipt.getPurchasedItems()) {
            System.out.printf("%s\t\t%d\t%d\n", item.getProduct().getName(), item.getQuantity(),
                    item.calculateTotalPrice().getAmount());
        }

        System.out.println("===========증정=============");
        for (String freeItem : receipt.getFreeItems()) {
            System.out.printf("%s\n", freeItem);
        }

        System.out.println("==============================");
        System.out.printf("총구매액\t\t%d\n", receipt.getTotalPrice());
        System.out.printf("행사할인\t\t-%d\n", receipt.getPromotionDiscount());
        System.out.printf("멤버십할인\t\t-%d\n", receipt.getMembershipDiscount());
        System.out.printf("내실돈\t\t%d\n", receipt.getFinalPrice());
    }

    @Override
    public void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    @Override
    public void printThankYouMessage() {
        System.out.println("감사합니다! W편의점을 이용해 주셔서 감사합니다.");
    }

    @Override
    public void close() {
        Console.close();
    }
}
