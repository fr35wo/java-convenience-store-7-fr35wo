package store.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PurchaseItemParser {

    private static final Pattern ITEM_PATTERN = Pattern.compile("\\[(.+?)-(\\d+)]");

    public List<CartItem> parse(String input, Inventory inventory) {
        validateInput(input);
        List<CartItem> items = parseItems(input, inventory);
        validateParsedItems(items);
        return items;
    }

    private void validateInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }
    }

    private List<CartItem> parseItems(String input, Inventory inventory) {
        List<CartItem> items = new ArrayList<>();
        Matcher matcher = ITEM_PATTERN.matcher(input);

        while (matcher.find()) {
            String productName = matcher.group(1).trim();
            int quantity = parseQuantity(matcher.group(2).trim());

            Product product = findProduct(productName, inventory);
            addItem(items, product, quantity);
        }

        return items;
    }

    private int parseQuantity(String quantityStr) {
        int quantity = Integer.parseInt(quantityStr);
        if (quantity <= 0) {
            throw new IllegalArgumentException("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }
        return quantity;
    }

    private Product findProduct(String productName, Inventory inventory) {
        Product promoProduct = inventory.getProductByNameAndPromotion(productName, true).orElse(null);
        Product regularProduct = inventory.getProductByNameAndPromotion(productName, false).orElse(null);

        if (promoProduct == null && regularProduct == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }

        if (promoProduct != null) {
            return promoProduct;
        }
        return regularProduct;
    }

    private void addItem(List<CartItem> items, Product product, int quantity) {
        items.add(new CartItem(product, quantity));
    }

    private void validateParsedItems(List<CartItem> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }
    }
}
