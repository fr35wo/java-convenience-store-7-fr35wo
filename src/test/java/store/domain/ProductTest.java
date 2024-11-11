package store.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import camp.nextstep.edu.missionutils.DateTimes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import store.common.ErrorMessages;

public class ProductTest {

    @Test
    void 정상적인_제품_생성() {
        Promotion promotion = new Promotion("여름 세일", 2, 1, DateTimes.now().toLocalDate().minusDays(1),
                DateTimes.now().toLocalDate().plusDays(5));
        Product product = new Product("밀키스", 1000, 10, promotion);

        assertThat(product.getName()).isEqualTo("밀키스");
        assertThat(product.getPrice().getAmount()).isEqualTo(1000);
        assertThat(product.getStock()).isEqualTo(10);
        assertThat(product.getPromotion().getName()).isEqualTo("여름 세일");
    }

    @Test
    void 제품_가격_계산() {
        Product product = new Product("게토레이", 1500, 20, null);
        Money totalPrice = product.calculatePrice(5);

        assertThat(totalPrice.getAmount()).isEqualTo(7500);
    }

    @ParameterizedTest
    @CsvSource({"10, 5, 5", "20, 10, 10", "15, 15, 0"})
    void 정규_재고_차감_성공(int initialStock, int reduceBy, int expectedStock) {
        Product product = new Product("주스", 2000, initialStock, null);
        product.reduceRegularStock(reduceBy);

        assertThat(product.getStock()).isEqualTo(expectedStock);
    }

    @Test
    void 정규_재고_차감_실패() {
        Product product = new Product("커피", 3000, 5, null);

        assertThatThrownBy(() -> product.reduceRegularStock(10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(ErrorMessages.EXCEED_STOCK);
    }

    @Test
    void 프로모션_재고_차감_성공() {
        Promotion promotion = new Promotion("가을 세일", 3, 1, DateTimes.now().toLocalDate().minusDays(2),
                DateTimes.now().toLocalDate().plusDays(3));
        Product product = new Product("홍차", 4000, 8, promotion);

        product.reducePromotionStock(3);
        assertThat(product.getStock()).isEqualTo(5);
    }

    @Test
    void 프로모션_재고_차감_실패() {
        Promotion promotion = new Promotion("겨울 세일", 2, 1, DateTimes.now().toLocalDate().minusDays(1),
                DateTimes.now().toLocalDate().plusDays(5));
        Product product = new Product("녹차", 5000, 3, promotion);

        assertThatThrownBy(() -> product.reducePromotionStock(5))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(ErrorMessages.EXCEED_STOCK);
    }

    @Test
    void 재고_추가_성공() {
        Product product = new Product("우유", 1200, 10, null);
        product.addStock(5);

        assertThat(product.getStock()).isEqualTo(15);
    }

    @Test
    void 프로모션_설명_확인() {
        Promotion promotion = new Promotion("봄 세일", 4, 1, DateTimes.now().toLocalDate().minusDays(1),
                DateTimes.now().toLocalDate().plusDays(10));
        Product product = new Product("초콜릿", 2000, 50, promotion);

        String promoDescription = product.getPromotionDescription();
        assertThat(promoDescription).isEqualTo("봄 세일");
    }

    @Test
    void 프로모션_없는_제품_설명_확인() {
        Product product = new Product("쿠키", 1000, 20, null);

        String promoDescription = product.getPromotionDescription();
        assertThat(promoDescription).isEqualTo("");
    }
}
