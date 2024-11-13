package store.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import store.common.ErrorMessages;

public class ParsedItemTest {

    @Test
    void 파싱된_아이템_생성_성공() {
        Inventory inventory = new Inventory();
        Product product = new Product("콜라", 1500, 10, null);
        int quantity = 5;

        assertDoesNotThrow(() -> new ParsedItem(product, quantity, inventory));
    }

    @ParameterizedTest
    @CsvSource({
            "밀키스, 15, 10",
            "게토레이, 6, 5",
            "환타, 3, 2"
    })
    void 파라미터화된_재고를_초과하는_파싱된_아이템_생성_실패(String productName, int quantity, int stock) {
        Inventory inventory = new Inventory();
        Product product = new Product(productName, 1000, stock, null);

        assertThatThrownBy(() -> new ParsedItem(product, quantity, inventory))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessages.EXCEED_STOCK);
    }
}
