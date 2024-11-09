package store.domain;

public class Membership {
    private final boolean isMember;

    public Membership(boolean isMember) {
        this.isMember = isMember;
    }

    public int calculateMembershipDiscount(int nonPromoTotal) {
        if (!isMember) {
            return 0;
        }
        return (int) Math.min(nonPromoTotal * 0.3, 8000);
    }
}

