package store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import camp.nextstep.edu.missionutils.DateTimes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ReceiptTest {

    private Cart cart;
    private Membership membership;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        membership = Membership.Y;
    }

    @ParameterizedTest
    @CsvSource({
            "사이다, 1000, 2, 콜라, 1500, 3",
            "초코바, 500, 1, 밀키스, 1300, 4",
            "사탕, 300, 5, 젤리, 800, 10"
    })
    void 유효한_영수증_생성_성공(String productName1, int price1, int quantity1,
                       String productName2, int price2, int quantity2) {
        Product product1 = new Product(productName1, price1, 100, null);
        Product product2 = new Product(productName2, price2, 100, null);

        CartItem item1 = new CartItem(product1, quantity1);
        CartItem item2 = new CartItem(product2, quantity2);

        cart.addItem(item1);
        cart.addItem(item2);

        Receipt receipt = new Receipt(cart);

        assertThat(receipt.getPurchasedItems()).containsExactly(item1, item2);
        assertThat(receipt.getTotalPrice()).isEqualTo(cart.getTotalPrice().getAmount());
        assertThat(receipt.getTotalQuantity()).isEqualTo(2);
    }

    @Test
    void 카트가_비어있을_때_총가격은_0원() {
        Receipt receipt = new Receipt(cart);

        assertThat(receipt.getTotalPrice()).isEqualTo(0);
    }

    @ParameterizedTest
    @CsvSource({
            "사이다, 1000, 3, 2100",
            "콜라, 2000, 1, 1400",
            "초코바, 1500, 5, 5250"
    })
    void 멤버십_할인_적용_성공(String productName, int price, int quantity, int expectedTotalPrice) {
        Product product = new Product(productName, price, 100, null);
        CartItem item = new CartItem(product, quantity);
        cart.addItem(item);

        Receipt receipt = new Receipt(cart);

        assertThat(receipt.getFinalPrice(cart, membership)).isEqualTo(expectedTotalPrice);
    }

    @Test
    void 프로모션_적용된_총가격_계산_성공() {
        Promotion promotion = new Promotion("Buy 1 Get 1", 1, 1, DateTimes.now().toLocalDate().minusDays(1),
                DateTimes.now().toLocalDate().plusDays(5));
        Product productWithPromotion = new Product("콜라", 1000, 20, promotion);
        CartItem item = new CartItem(productWithPromotion, 2);
        cart.addItem(item);

        Receipt receipt = new Receipt(cart);

        assertThat(receipt.getFinalPrice(cart, membership)).isEqualTo(1000);
    }

}
