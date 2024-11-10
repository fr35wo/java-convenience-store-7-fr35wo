package store.io.output.impl;

import camp.nextstep.edu.missionutils.Console;
import java.util.List;
import java.util.Optional;
import store.common.ConsoleMessages;
import store.common.ErrorMessages;
import store.domain.CartItem;
import store.domain.Product;
import store.domain.Receipt;
import store.io.output.StoreOutput;

public class OutputConsole implements StoreOutput {

    @Override
    public void printProductList(List<Product> products) {
        printHeader();
        printProducts(products);
    }

    private void printHeader() {
        System.out.println(ConsoleMessages.WELCOME_MESSAGE);
        System.out.println(ConsoleMessages.PRODUCT_LIST_HEADER);
        System.out.print(ConsoleMessages.LINE_SEPARATOR);
    }

    private void printProducts(List<Product> products) {
        for (Product product : products) {
            printProductInfo(product);
            printRegularProductInfoIfMissing(products, product);
        }
    }

    private void printProductInfo(Product product) {
        String stockInfo = getStockInfo(product);
        String promoInfo = product.getPromotionDescription();
        String formattedPrice = getFormattedPrice(product);
        System.out.printf("- %s %s원 %s %s%n", product.getName(), formattedPrice, stockInfo, promoInfo);
    }

    private void printRegularProductInfoIfMissing(List<Product> products, Product product) {
        if (product.getPromotion() == null) {
            return;
        }
        Optional<Product> regularProduct = findRegularProduct(products, product);
        if (regularProduct.isEmpty()) {
            String formattedPrice = getFormattedPrice(product);
            System.out.printf("- %s %s원 재고 없음%n", product.getName(), formattedPrice);
        }
    }

    private String getStockInfo(Product product) {
        return product.getStock() > 0 ? product.getStock() + "개" : "재고 없음";
    }

    private String getFormattedPrice(Product product) {
        return String.format("%,d", product.getPrice().getAmount());
    }

    private Optional<Product> findRegularProduct(List<Product> products, Product product) {
        return products.stream()
                .filter(p -> p.getName().equals(product.getName()) && p.getPromotion() == null)
                .findFirst();
    }

    @Override
    public void printReceipt(Receipt receipt) {
        printReceiptHeader();
        printPurchasedItems(receipt);
        printFreeItems(receipt);
        printReceiptSummary(receipt);
    }

    private void printReceiptHeader() {
        System.out.print(ConsoleMessages.LINE_SEPARATOR);
        System.out.printf("==============%-3s%s================%n", "W", "편의점");
        System.out.printf("%-10s %10s %8s%n", "상품명", "수량", "금액");
    }

    private void printPurchasedItems(Receipt receipt) {
        for (CartItem item : receipt.getPurchasedItems()) {
            printPurchasedItem(item);
        }
    }

    private void printPurchasedItem(CartItem item) {
        String productName = item.getProduct().getName();
        int quantity = item.getQuantity();
        String totalPrice = String.format("%,d", item.calculateTotalPrice().getAmount());

        if (productName.length() < 3) {
            System.out.printf("%-10s %10d %13s%n", productName, quantity, totalPrice);
            return;
        }
        System.out.printf("%-10s %9d %14s%n", productName, quantity, totalPrice);

    }

    private void printFreeItems(Receipt receipt) {
        System.out.printf("=============%-7s%s===============%n", "증", "정");
        for (CartItem item : receipt.getFreeItems()) {
            printFreeItem(item);
        }
    }

    private void printFreeItem(CartItem item) {
        String productName = item.getProduct().getName();
        int quantity = item.getFreeQuantity();
        System.out.printf("%-10s %9d%n", productName, quantity);
    }

    private void printReceiptSummary(Receipt receipt) {
        System.out.println("====================================");
        System.out.printf("%-10s %8d %13s%n", "총구매액", receipt.getTotalQuantity(),
                String.format("%,d", receipt.getTotalPrice()));
        System.out.printf("%-10s %22s%n", "행사할인",
                String.format("-%s", String.format("%,d", receipt.getPromotionDiscount())));
        System.out.printf("%-10s %30s%n", "멤버십할인",
                String.format("-%s", String.format("%,d", receipt.getMembershipDiscount())));
        System.out.printf("%-10s %23s%n", "내실돈", String.format("%,d", receipt.getFinalPrice()));
    }

    @Override
    public void printError(String message) {
        System.out.println(ErrorMessages.ERROR_MESSAGE + message);
    }

    @Override
    public void close() {
        Console.close();
    }
}
