package store.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class PromotionTest {

    @Test
    void 유효한_프로모션_계산_테스트() {
        Promotion promotion = new Promotion("Buy 1 Get 1", 1, 1, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        int purchasedQuantity = 4;
        int stock = 10;

        int freeItems = promotion.calculateFreeItems(purchasedQuantity, stock);

        assertThat(freeItems).isEqualTo(2);
    }

    @Test
    void 유효하지_않은_프로모션_계산_테스트() {
        Promotion promotion = new Promotion("Buy 1 Get 1", 1, 1, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
        int purchasedQuantity = 4;
        int stock = 10;

        int freeItems = promotion.calculateFreeItems(purchasedQuantity, stock);

        assertThat(freeItems).isEqualTo(0);
    }

    @ParameterizedTest
    @CsvSource({
            "2024-01-01, 2024-12-31, 2024-06-01, true",
            "2024-01-01, 2024-12-31, 2023-12-31, false",
            "2024-01-01, 2024-12-31, 2025-01-01, false"
    })
    void 프로모션_유효성_검사_테스트(String startDate, String endDate, String checkDate, boolean expectedResult) {
        Promotion promotion = new Promotion("Seasonal Discount", 2, 1, LocalDate.parse(startDate),
                LocalDate.parse(endDate));
        LocalDate dateToCheck = LocalDate.parse(checkDate);

        boolean isValid = promotion.isValid(dateToCheck);

        assertThat(isValid).isEqualTo(expectedResult);
    }

    @Test
    void 프로모션_생성_테스트() {
        String name = "Buy 2 Get 1";
        int buyQuantity = 2;
        int freeQuantity = 1;
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 30);

        Promotion promotion = new Promotion(name, buyQuantity, freeQuantity, startDate, endDate);

        assertThat(promotion.getName()).isEqualTo(name);
        assertThat(promotion.getBuyQuantity()).isEqualTo(buyQuantity);
        assertThat(promotion.getFreeQuantity()).isEqualTo(freeQuantity);
        assertThat(promotion.isValid(LocalDate.of(2024, 4, 1))).isTrue();
    }

    @Test
    void 프로모션_예외_케이스_테스트() {
        Promotion promotion = new Promotion("Invalid Promotion", 1, 1, LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31));
        int purchasedQuantity = 5;
        int stock = 0;

        int freeItems = promotion.calculateFreeItems(purchasedQuantity, stock);

        assertThat(freeItems).isEqualTo(0);
    }

    @Test
    void buyQuantity_가_0인_경우_프로모션_예외() {
        assertThatThrownBy(() -> new Promotion("Invalid Buy Quantity", 0, 1,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("buyQuantity, freeQuantity는 0보다 커야 합니다.");
    }

    @Test
    void freeQuantity_가_0인_경우_프로모션_예외() {
        assertThatThrownBy(() -> new Promotion("Invalid Buy freeQuantity", 1, 0,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("buyQuantity, freeQuantity는 0보다 커야 합니다.");
    }

    @ParameterizedTest
    @CsvSource({
            "2024-01-01, 2023-12-31, 2024-01-01, false"
    })
    void 유효하지_않은_날짜의_프로모션_생성_실패(String startDate, String endDate, String checkDate, boolean expectedResult) {
        assertThatThrownBy(() -> new Promotion("Invalid Date Promotion", 1, 1,
                LocalDate.parse(startDate), LocalDate.parse(endDate)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("프로모션 날짜가 유효하지 않습니다.");
    }
}
