package store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import camp.nextstep.edu.missionutils.DateTimes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CartItemTest {
    private Product product;
    private Promotion promotion;
    private CartItem cartItem;

    @BeforeEach
    void setup() {
        promotion = new Promotion("Buy 2 Get 1 Free", 2, 1, DateTimes.now().toLocalDate().minusDays(1),
                DateTimes.now().toLocalDate().plusDays(1));
        product = new Product("Sample Product", 1000, 10, promotion);
        cartItem = new CartItem(product, 5);
    }

    @Test
    void 총가격_계산_성공() {
        Money totalPrice = cartItem.calculateTotalPrice();
        assertThat(totalPrice.getAmount()).isEqualTo(5000);
    }

    @ParameterizedTest
    @CsvSource({"5, 1000", "7, 2000", "15, 3000"})
    void 프로모션할인_계산_성공(int quantity, int expectedDiscount) {
        CartItem cartItem = new CartItem(product, quantity);
        int discount = cartItem.calculatePromotionDiscount();
        assertThat(discount).isEqualTo(expectedDiscount);
    }

    @Test
    void 프로모션할인_없는경우_계산_성공() {
        Product noPromoProduct = new Product("No Promo Product", 1000, 10, null);
        CartItem cartItem = new CartItem(noPromoProduct, 5);
        int discount = cartItem.calculatePromotionDiscount();
        assertThat(discount).isEqualTo(0);
    }

    @Test
    void 유효하지않은_프로모션_적용_예외() {
        Promotion expiredPromotion = new Promotion("Expired Promo", 2, 1, DateTimes.now().toLocalDate().minusDays(10),
                DateTimes.now().toLocalDate().minusDays(1));
        Product expiredProduct = new Product("Expired Product", 1000, 10, expiredPromotion);
        CartItem cartItem = new CartItem(expiredProduct, 5);

        boolean isValid = cartItem.isPromotionValid();
        assertThat(isValid).isFalse();
    }

    @Test
    void 프로모션_재고부족_예외() {
        Product lowStockProduct = new Product("Low Stock Product", 1000, 1, promotion);
        CartItem cartItem = new CartItem(lowStockProduct, 6);

        boolean stockAvailable = cartItem.checkPromotionStock();
        assertThat(stockAvailable).isTrue();
    }

    @Test
    void 유효한_프로모션_재고_확인() {
        boolean stockAvailable = cartItem.checkPromotionStock();
        assertThat(stockAvailable).isFalse();
    }

    @Test
    void 무료수량_계산_성공() {
        int freeQuantity = cartItem.getFreeQuantity();
        assertThat(freeQuantity).isEqualTo(1);
    }

    @Test
    void 남은수량_계산_성공() {
        int remainingQuantity = cartItem.calculateRemainingQuantity();
        assertThat(remainingQuantity).isEqualTo(0);
    }

    @Test
    void 추가_필요한_수량_계산_성공() {
        int additionalQuantity = cartItem.calculateAdditionalQuantityNeeded();
        assertThat(additionalQuantity).isEqualTo(1);
    }

    @Test
    void 수량_업데이트_후_아이템_생성_성공() {
        CartItem updatedItem = cartItem.withUpdatedQuantityForFullPrice(10);
        assertThat(updatedItem.getQuantity()).isEqualTo(10);
    }

    @Test
    void 추가_수량_적용_후_아이템_생성_성공() {
        CartItem updatedItem = cartItem.withAdditionalQuantity(3);
        assertThat(updatedItem.getQuantity()).isEqualTo(8);
    }
}
