package store.domain;

public class Money {
    private final int amount;

    public Money(int amount) {
        validateAmount(amount);
        this.amount = amount;
    }

    private void validateAmount(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("금액은 음수일 수 없습니다.");
        }
    }

    public Money add(Money other) {
        return new Money(this.amount + other.amount);
    }

    public Money subtract(Money other) {
        int result = this.amount - other.amount;
        if (result < 0) {
            throw new IllegalArgumentException("금액이 음수가 될 수 없습니다.");
        }
        return new Money(result);
    }

    public Money multiply(int multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("곱셈의 값은 음수가 될 수 없습니다.");
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
