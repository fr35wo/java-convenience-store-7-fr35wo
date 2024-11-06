package store.domain;

import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;

public class CartItem {
    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        if (isPromotionValid() && !checkPromotionStock()) {
            promptForAdditionalQuantity();
        }
    }

    /**
     * 총 금액을 전체 수량 기준으로 계산
     */
    public Money calculateTotalPrice() {
        return product.getPrice().multiply(quantity);
    }

    /**
     * 프로모션 적용 후 지불해야 할 금액을 반환
     */
    public Money getTotalAmountWithoutPromotion() {
        int effectivePaidQuantity = getEffectivePaidQuantity();
        return product.getPrice().multiply(effectivePaidQuantity);
    }

    private boolean isPromotionValid() {
        LocalDate currentDate = DateTimes.now().toLocalDate();
        Promotion promotion = product.getPromotion();
        return promotion != null && promotion.isValid(currentDate);
    }

    /**
     * 프로모션 재고를 우선적으로 사용하고, 부족할 경우 추가 결제 여부를 확인
     */
    private boolean checkPromotionStock() {
        Promotion promotion = product.getPromotion();
        if (promotion != null && promotion.isValid(DateTimes.now().toLocalDate())) {
            int buyQuantity = promotion.getBuyQuantity();
            int freeQuantity = promotion.getFreeQuantity();
            int totalRequired = buyQuantity + freeQuantity;

            int promoAvailableQuantity = (product.getStock() / totalRequired) * totalRequired;
            int remainingQuantity = quantity - promoAvailableQuantity;

            if (remainingQuantity > 0) {
                System.out.printf("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)\n",
                        product.getName(), remainingQuantity);

                String response = Console.readLine().trim();
                if ("N".equalsIgnoreCase(response)) {
                    quantity = promoAvailableQuantity;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 프로모션 조건에 따라 실제 지불해야 할 수량을 계산
     */
    public int getEffectivePaidQuantity() {
        Promotion promotion = product.getPromotion();
        if (promotion != null && promotion.isValid(LocalDate.now())) {
            int buyQuantity = promotion.getBuyQuantity();
            int freeQuantity = promotion.getFreeQuantity();
            int totalRequired = buyQuantity + freeQuantity;
            int quotient = quantity / totalRequired;
            int remainder = quantity % totalRequired;

            if (remainder > buyQuantity) {
                return (quotient + 1) * buyQuantity;
            } else {
                return quotient * buyQuantity + remainder;
            }
        }
        return quantity;  // 프로모션이 없으면 전체 수량을 지불
    }

    /**
     * 프로모션 조건에 따라 무료로 제공할 수량 계산
     */
    public int getFreeQuantity() {
        Promotion promotion = product.getPromotion();
        if (promotion != null && promotion.isValid(LocalDate.now())) {
            int buyQuantity = promotion.getBuyQuantity();
            int freeQuantity = promotion.getFreeQuantity();
            int totalRequired = buyQuantity + freeQuantity;
            int quotient = quantity / totalRequired;
            int remainder = quantity % totalRequired;

            if (remainder > buyQuantity) {
                return (quotient + 1) * freeQuantity;
            } else {
                return quotient * freeQuantity;
            }
        }
        return 0;
    }

    /**
     * 혜택 안내: 프로모션 적용이 가능한 경우 추가 혜택 안내 메시지 출력
     */
    private void promptForAdditionalQuantity() {
        Promotion promotion = product.getPromotion();
        if (promotion != null && promotion.isValid(LocalDate.now())) {
            int buyQuantity = promotion.getBuyQuantity();
            int freeQuantity = promotion.getFreeQuantity();
            int totalRequired = buyQuantity + freeQuantity;
            int remainder = quantity % totalRequired;

            if (remainder == buyQuantity) {
                int additionalQuantityNeeded = totalRequired - remainder;
                System.out.printf("현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\n",
                        product.getName(), additionalQuantityNeeded);
                String response = Console.readLine().trim();
                if ("Y".equalsIgnoreCase(response)) {
                    quantity += additionalQuantityNeeded;
                }
            }
        }
    }

    public String getProductName() {
        return product.getName();
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }
}
