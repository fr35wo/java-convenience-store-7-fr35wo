package store.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
    }

    @Test
    void 프로모션_파일_로드_성공() {
        assertThat(inventory.getProductByNameAndPromotion("오렌지주스", true)).isPresent();
    }

    @Test
    void 제품_파일_로드_성공() {
        assertThat(inventory.getProductByNameAndPromotion("콜라", false)).isPresent();
    }

    @ParameterizedTest
    @CsvSource({
            "오렌지주스, true",
            "정식도시락, false"
    })
    void 제품_찾기_성공(String productName, boolean hasPromotion) {
        Optional<Product> product = inventory.getProductByNameAndPromotion(productName, hasPromotion);
        assertThat(product).isPresent();
    }

    @Test
    void 유효하지_않은_제품_찾기_실패() {
        Optional<Product> product = inventory.getProductByNameAndPromotion("InvalidItem", false);
        assertThat(product).isEmpty();
    }

    @Test
    void 재고_업데이트_성공() {
        Product product = inventory.getProductByNameAndPromotion("물", false).orElseThrow();
        Cart cart = new Cart();
        cart.addItem(new CartItem(product, 1));

        inventory.updateInventory(cart);

        int remainingStock = product.getStock();
        assertThat(remainingStock).isEqualTo(9);
    }

    @Test
    void 재고_초과_구매_실패() {
        Product product = inventory.getProductByNameAndPromotion("물", false).orElseThrow();
        Cart cart = new Cart();
        cart.addItem(new CartItem(product, 11));

        assertThatThrownBy(() -> inventory.updateInventory(cart))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
    }

    @Test
    void 유효하지_않은_프로모션_할당_실패() {
        LocalDate currentDate = LocalDate.of(2025, 1, 1);
        Promotion expiredPromotion = new Promotion("ExpiredPromo", 2, 1, LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31));

        assertThat(expiredPromotion.isValid(currentDate)).isFalse();
    }

    @Test
    void 프로모션_제품_할인_적용_성공() {
        Product product = inventory.getProductByNameAndPromotion("사이다", true).orElseThrow();
        CartItem cartItem = new CartItem(product, 5);
        int promotionDiscount = cartItem.calculatePromotionDiscount();

        assertThat(promotionDiscount).isGreaterThan(0);
    }

    @Test
    void 프로모션_없음_할인_적용_실패() {
        Product product = inventory.getProductByNameAndPromotion("에너지바", false).orElseThrow();
        CartItem cartItem = new CartItem(product, 5);
        int promotionDiscount = cartItem.calculatePromotionDiscount();

        assertThat(promotionDiscount).isEqualTo(0);
    }
}
