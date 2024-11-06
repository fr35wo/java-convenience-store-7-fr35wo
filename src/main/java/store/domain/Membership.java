package store.domain;

public class Membership {
    private final boolean isMember;

    public Membership(boolean isMember) {
        this.isMember = isMember;
    }

    public Money getDiscount(Money amount) {
        if (!isMember) {
            return new Money(0);
        }
        int discount = (int) Math.min(amount.getAmount() * 0.3, 8000);
        return new Money(discount);
    }

    public boolean isMember() {
        return isMember;
    }
}

