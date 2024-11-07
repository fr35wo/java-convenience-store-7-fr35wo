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

    public Money calculateTotalPrice() {
        return product.getPrice().multiply(quantity);
    }

    public Money getTotalAmountWithoutPromotion() {
        int effectivePaidQuantity = getEffectivePaidQuantity();
        return product.getPrice().multiply(effectivePaidQuantity);
    }

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
        return quantity;
    }

    private boolean isPromotionValid() {
        LocalDate currentDate = DateTimes.now().toLocalDate();
        Promotion promotion = product.getPromotion();
        return promotion != null && promotion.isValid(currentDate);
    }

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

                String response = getValidYNResponse();
                if ("N".equals(response)) {
                    quantity = promoAvailableQuantity;
                }
                return true;
            }
        }
        return false;
    }

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

                String response = getValidYNResponse();
                if ("Y".equals(response)) {
                    quantity += additionalQuantityNeeded;
                }
            }
        }
    }

    private String getValidYNResponse() {
        while (true) {
            String response = Console.readLine().trim();
            if (response.equals("Y") || response.equals("N")) {
                return response;
            } else {
                throw new IllegalArgumentException("잘못된 입력입니다. 'Y' 또는 'N'만 입력해 주세요.");
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

    // 프로모션이 적용되는지 여부를 확인하는 메서드 추가
    public boolean hasPromotion() {
        return product.getPromotion() != null && product.getPromotion().isValid(DateTimes.now().toLocalDate());
    }
}
