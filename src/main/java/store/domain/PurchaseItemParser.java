package store.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import store.common.ErrorMessages;

public class PurchaseItemParser {
    private static final int INITIAL_LIST_SIZE = 0;
    private static final int INVALID_QUANTITY_VALUE = 0;
    private static final int PRODUCT_NAME_GROUP_INDEX = 1;
    private static final int QUANTITY_GROUP_INDEX = 2;

    private static final String OPEN_BRACKET = Pattern.quote("[");
    private static final String CLOSE_BRACKET = Pattern.quote("]");
    private static final String DASH = Pattern.quote("-");
    private static final String COMMA = Pattern.quote(",");

    private static final Pattern ITEM_PATTERN = Pattern.compile(
            OPEN_BRACKET + "([^\\-\\]\\s]+)" + DASH + "(\\d+)" + CLOSE_BRACKET);

    public List<ParsedItem> parse(String input, Inventory inventory) {
        validateInput(input);
        return parseItems(input, inventory);
    }

    private void validateInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_INPUT_FORMAT);
        }

        String validPattern = OPEN_BRACKET + "[^\\-\\]\\s]+" + DASH + "\\d+" + CLOSE_BRACKET +
                "(" + COMMA + OPEN_BRACKET + "[^\\-\\]\\s]+" + DASH + "\\d+" + CLOSE_BRACKET + ")*";
        if (!input.matches(validPattern)) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_INPUT_FORMAT);
        }
    }

    private List<ParsedItem> parseItems(String input, Inventory inventory) {
        List<ParsedItem> items = new ArrayList<>(INITIAL_LIST_SIZE);
        Matcher matcher = ITEM_PATTERN.matcher(input);

        while (matcher.find()) {
            String productName = matcher.group(PRODUCT_NAME_GROUP_INDEX).trim();
            if (productName.isEmpty()) {
                throw new IllegalArgumentException(ErrorMessages.INVALID_INPUT_FORMAT);
            }

            int quantity = parseQuantity(matcher.group(QUANTITY_GROUP_INDEX).trim());

            Product product = findProduct(productName, inventory);
            items.add(new ParsedItem(product, quantity, inventory));
        }

        return items;
    }

    private int parseQuantity(String quantityStr) {
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= INVALID_QUANTITY_VALUE) {
                throw new IllegalArgumentException(ErrorMessages.INVALID_INPUT_FORMAT);
            }
            return quantity;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_INPUT_FORMAT);
        }
    }

    private Product findProduct(String productName, Inventory inventory) {
        Product promoProduct = inventory.getProductByNameAndPromotion(productName, true).orElse(null);
        Product regularProduct = inventory.getProductByNameAndPromotion(productName, false).orElse(null);

        if (promoProduct == null && regularProduct == null) {
            throw new IllegalArgumentException(ErrorMessages.PRODUCT_NOT_FOUND);
        }

        return promoProduct != null ? promoProduct : regularProduct;
    }
}
