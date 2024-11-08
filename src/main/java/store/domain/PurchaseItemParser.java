package store.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PurchaseItemParser {

    // 정규 표현식 패턴 정의
    private static final Pattern ITEM_PATTERN = Pattern.compile("\\[(.+?)-(\\d+)]");

    public List<ParsedItem> parse(String input, Inventory inventory) {
        validateInput(input);
        return parseItems(input, inventory);
    }

    // 입력 전체를 검증하는 메서드
    private void validateInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }

        // 전체 입력이 정규 표현식의 반복 패턴과 정확히 일치해야 함
        String validPattern = "(\\[(.+?)-(\\d+)\\])(,\\[(.+?)-(\\d+)\\])*";
        if (!input.matches(validPattern)) {
            throw new IllegalArgumentException("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }
    }

    // 입력을 파싱하여 ParsedItem 리스트를 반환하는 메서드
    private List<ParsedItem> parseItems(String input, Inventory inventory) {
        List<ParsedItem> items = new ArrayList<>();
        Matcher matcher = ITEM_PATTERN.matcher(input);

        while (matcher.find()) {
            String productName = matcher.group(1).trim();
            int quantity = parseQuantity(matcher.group(2).trim());

            Product product = findProduct(productName, inventory);
            items.add(new ParsedItem(product, quantity));
        }

        return items;
    }

    // 수량을 파싱하고 검증하는 메서드
    private int parseQuantity(String quantityStr) {
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new IllegalArgumentException("수량은 0보다 커야 합니다. 다시 입력해 주세요.");
            }
            return quantity;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("올바르지 않은 형식의 수량입니다. 다시 입력해 주세요.");
        }
    }

    // 상품을 찾는 메서드
    private Product findProduct(String productName, Inventory inventory) {
        Product promoProduct = inventory.getProductByNameAndPromotion(productName, true).orElse(null);
        Product regularProduct = inventory.getProductByNameAndPromotion(productName, false).orElse(null);

        if (promoProduct == null && regularProduct == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }

        return promoProduct != null ? promoProduct : regularProduct;
    }
}
