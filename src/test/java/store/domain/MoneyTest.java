package store.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import store.common.ErrorMessages;

public class MoneyTest {

    @Test
    void 생성자_정상_케이스() {
        Money money = new Money(1000);
        assertThat(money.getAmount()).isEqualTo(1000);
    }

    @Test
    void 생성자_음수_예외_케이스() {
        assertThatThrownBy(() -> new Money(-500))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessages.NEGATIVE_AMOUNT);
    }

    @ParameterizedTest
    @CsvSource({"1000, 500, 1500", "0, 0, 0", "200, 300, 500"})
    void 덧셈_정상_케이스(int amount1, int amount2, int expected) {
        Money money1 = new Money(amount1);
        Money money2 = new Money(amount2);
        Money result = money1.add(money2);
        assertThat(result.getAmount()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"1000, 500, 500", "500, 200, 300", "0, 0, 0"})
    void 뺄셈_정상_케이스(int amount1, int amount2, int expected) {
        Money money1 = new Money(amount1);
        Money money2 = new Money(amount2);
        Money result = money1.subtract(money2);
        assertThat(result.getAmount()).isEqualTo(expected);
    }

    @Test
    void 뺄셈_음수_예외_케이스() {
        Money money1 = new Money(300);
        Money money2 = new Money(500);
        assertThatThrownBy(() -> money1.subtract(money2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessages.NEGATIVE_AMOUNT);
    }

    @ParameterizedTest
    @CsvSource({"100, 2, 200", "500, 0, 0", "300, 3, 900"})
    void 곱셈_정상_케이스(int amount, int multiplier, int expected) {
        Money money = new Money(amount);
        Money result = money.multiply(multiplier);
        assertThat(result.getAmount()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100})
    void 곱셈_음수_예외_케이스(int multiplier) {
        Money money = new Money(1000);
        assertThatThrownBy(() -> money.multiply(multiplier))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessages.NEGATIVE_MULTIPLICATION);
    }

    @ParameterizedTest
    @CsvSource({"1000, 0, 0", "0, 5, 0"})
    void 곱셈_0_처리_케이스(int amount, int multiplier, int expected) {
        Money money = new Money(amount);
        Money result = money.multiply(multiplier);
        assertThat(result.getAmount()).isEqualTo(expected);
    }
}
