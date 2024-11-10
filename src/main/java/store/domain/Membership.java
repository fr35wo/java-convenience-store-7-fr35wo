package store.domain;

public enum Membership {
    Y(30, 8000),
    N(0, 0);

    private final int discountRate;
    private final int maxDiscountAmount;

    Membership(int discountRate, int maxDiscountAmount) {
        this.discountRate = discountRate;
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public int calculateDiscount(int nonPromoTotal) {
        int discountAmount = (nonPromoTotal * discountRate) / 100;
        return Math.min(discountAmount, maxDiscountAmount);
    }
}
