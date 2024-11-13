package store.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import store.common.ErrorMessages;

public class PurchaseItemParserTest {

    private PurchaseItemParser parser;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        parser = new PurchaseItemParser();
        inventory = new Inventory();
    }

    @ParameterizedTest
    @CsvSource({
            "[사이다-2],[콜라-3]",
            "[에너지바-1]",
            "[정식도시락-5],[초코바-10]"
    })
    void 유효한_아이템_목록_파싱_성공(String input) {
        List<ParsedItem> items = parser.parse(input, inventory);

        assertThat(items).isNotEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "InvalidInput,",
            "[사이다-abc]",
            "[콜라-0]"
    })
    void 유효하지_않은_아이템_목록_파싱_실패(String input) {
        assertThatThrownBy(() -> parser.parse(input, inventory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessages.INVALID_INPUT_FORMAT);
    }

}
