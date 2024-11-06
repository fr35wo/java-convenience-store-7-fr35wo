package store.domain;

import java.util.List;

public class Cart {
    private final List<CartItem> items;

    public Cart(List<CartItem> items) {
        this.items = items;
    }

    // 총 가격 계산 (프로모션 및 멤버십 할인 적용 후 금액 반환)
    public Money calculateTotal(Membership membership) {
        return items.stream()
                .map(item -> item.calculateTotalPrice())
                .reduce(new Money(0), Money::add);
    }

    public List<CartItem> getItems() {
        return items;
    }
}
