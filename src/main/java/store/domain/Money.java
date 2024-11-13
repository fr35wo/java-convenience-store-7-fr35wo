package store.domain;

import store.common.ErrorMessages;

public class Money {
    private static final int MINIMUM_AMOUNT = 0;

    private final int amount;

    public Money(int amount) {
        validateAmount(amount);
        this.amount = amount;
    }

    private void validateAmount(int amount) {
        if (amount < MINIMUM_AMOUNT) {
            throw new IllegalArgumentException(ErrorMessages.NEGATIVE_AMOUNT);
        }
    }

    public Money add(Money other) {
        return new Money(this.amount + other.amount);
    }

    public Money subtract(Money other) {
        int result = this.amount - other.amount;
        if (result < MINIMUM_AMOUNT) {
            throw new IllegalArgumentException(ErrorMessages.NEGATIVE_AMOUNT);
        }
        return new Money(result);
    }

    public Money multiply(int multiplier) {
        if (multiplier < MINIMUM_AMOUNT) {
            throw new IllegalArgumentException(ErrorMessages.NEGATIVE_MULTIPLICATION);
        }
        return new Money(this.amount * multiplier);
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.valueOf(amount);
    }
}
