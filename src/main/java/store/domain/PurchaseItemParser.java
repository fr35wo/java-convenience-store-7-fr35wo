package store.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PurchaseItemParser {

    private static final Pattern ITEM_PATTERN = Pattern.compile("\\[(.+?)-(\\d+)]");

    /**
     * 사용자 입력 문자열을 파싱하여 CartItem 리스트로 변환
     *
     * @param input     사용자로부터 입력된 구매 항목 문자열
     * @param inventory 현재 상품 목록을 포함한 재고 정보
     * @return 파싱된 CartItem 리스트
     * @throws IllegalArgumentException 유효하지 않은 입력 형식 또는 재고에 없는 상품일 때
     */
    public List<CartItem> parse(String input, Inventory inventory) {
        List<CartItem> items = new ArrayList<>();

        // 입력이 없거나 형식이 맞지 않는 경우 예외 발생
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }

        Matcher matcher = ITEM_PATTERN.matcher(input);

        while (matcher.find()) {
            String productName = matcher.group(1).trim();
            int quantity = Integer.parseInt(matcher.group(2).trim());

            // 수량이 유효하지 않으면 예외 발생
            if (quantity <= 0) {
                throw new IllegalArgumentException("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
            }

            // 프로모션 상품과 일반 상품을 각각 가져와서 존재 여부 확인
            Product promoProduct = inventory.getProductByNameAndPromotion(productName, true).orElse(null);
            Product regularProduct = inventory.getProductByNameAndPromotion(productName, false).orElse(null);

            if (promoProduct == null && regularProduct == null) {
                throw new IllegalArgumentException("존재하지 않는 상품입니다. 다시 입력해 주세요.");
            }

            // 프로모션 상품이 있는 경우, 프로모션 재고가 우선적으로 할당됨
            if (promoProduct != null && quantity > 0) {
                items.add(new CartItem(promoProduct, quantity));
            } else if (regularProduct != null) {  // 일반 상품이 있는 경우, 남은 수량만큼 일반 상품을 추가
                items.add(new CartItem(regularProduct, quantity));
            }
        }

        // 입력 형식이 맞지 않으면 예외 발생
        if (items.isEmpty()) {
            throw new IllegalArgumentException("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }

        return items;
    }
}
