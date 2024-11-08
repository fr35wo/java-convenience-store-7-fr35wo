package store.domain;

public class Membership {
    private final boolean isMember;

    public Membership(boolean isMember) {
        this.isMember = isMember;
    }

    public boolean isMember() {
        return isMember;
    }
}

