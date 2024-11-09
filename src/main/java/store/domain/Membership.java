package store.domain;

public enum Membership {
    Y(30, 8000),
    N(0, 0);

    private final int discountRate; // 할인율
    private final int maxDiscountAmount; // 최대 할인 금액

    Membership(int discountRate, int maxDiscountAmount) {
        this.discountRate = discountRate;
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public int calculateDiscount(int nonPromoTotal) {
        int discountAmount = (nonPromoTotal * discountRate) / 100;
        return Math.min(discountAmount, maxDiscountAmount);
    }
}