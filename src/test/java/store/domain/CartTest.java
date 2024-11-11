package store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import camp.nextstep.edu.missionutils.DateTimes;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CartTest {
    private Cart cart;
    private CartItem item1;
    private CartItem item2;

    @BeforeEach
    void 초기화() {
        cart = new Cart();
        item1 = new CartItem(new Product("상품1", 1000, 10, null), 2);
        item2 = new CartItem(new Product("상품2", 2000, 5, null), 1);
    }

    @Test
    void 아이템_교체_성공() {
        cart.addItem(item1);
        CartItem newItem = new CartItem(new Product("상품3", 1500, 8, null), 3);
        cart.replaceItem(0, newItem);

        List<CartItem> items = cart.getItems();
        assertThat(items).hasSize(1);
        assertThat(items.getFirst()).isEqualTo(newItem);
    }

    @ParameterizedTest
    @CsvSource({
            "1000, 2, 2000",
            "2000, 3, 6000",
            "1500, 1, 1500"
    })
    void 아이템_가격에_따른_총_가격_계산(int price, int quantity, int expectedTotalPrice) {
        CartItem item = new CartItem(new Product("테스트상품", price, 10, null), quantity);
        cart.addItem(item);

        Money totalPrice = cart.getTotalPrice();
        assertThat(totalPrice.getAmount()).isEqualTo(expectedTotalPrice);
    }

    @Test
    void 총_프로모션_할인_금액_계산() {
        Product productWithPromotion = new Product("상품1", 1000, 10,
                new Promotion("프로모션1", 1, 1, DateTimes.now().toLocalDate().minusDays(1),
                        DateTimes.now().toLocalDate().plusDays(1)));
        CartItem promotionalItem = new CartItem(productWithPromotion, 2);
        cart.addItem(promotionalItem);

        int totalDiscount = cart.getTotalPromotionDiscount();
        assertThat(totalDiscount).isEqualTo(1000);
    }

    @Test
    void 총_프로모션_없는_금액_계산() {
        cart.addItem(item1);
        cart.addItem(item2);

        int totalNonPromoAmount = cart.getTotalNonPromoAmount();
        assertThat(totalNonPromoAmount).isEqualTo(4000);
    }

    @Test
    void 아이템_추가_및_수량_확인() {
        cart.addItem(item1);
        cart.addItem(item2);

        List<CartItem> items = cart.getItems();

        assertThat(items).hasSize(2);
        assertThat(items).contains(item1, item2);
        assertThat(items.get(0).getQuantity()).isEqualTo(2);
        assertThat(items.get(1).getQuantity()).isEqualTo(1);
    }

    @Test
    void 최대_수용량_아이템_추가_성공() {
        for (int i = 0; i < 100; i++) {
            cart.addItem(new CartItem(new Product("상품" + i, 1000, 10, null), 1));
        }

        assertThat(cart.getItems()).hasSize(100);
    }

}
