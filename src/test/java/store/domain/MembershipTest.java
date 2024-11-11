package store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MembershipTest {

    @ParameterizedTest
    @CsvSource({
            "Y, 10000, 3000",
            "Y, 200000, 8000",
            "N, 100000, 0",
            "Y, 0, 0",
            "N, 0, 0"
    })
    void 멤버십할인계산_성공케이스(String membershipType, int nonPromoTotal, int expectedDiscount) {
        Membership membership = Membership.valueOf(membershipType);
        int discount = membership.calculateDiscount(nonPromoTotal);

        assertThat(discount).isEqualTo(expectedDiscount);
    }

    @ParameterizedTest
    @CsvSource({
            "Y, 10000, 3000",
            "Y, 500000, 8000",
            "N, 500000, 0"
    })
    void 멤버십할인계산_다양한경우(String membershipType, int nonPromoTotal, int expectedDiscount) {
        Membership membership = Membership.valueOf(membershipType);
        int discount = membership.calculateDiscount(nonPromoTotal);

        assertThat(discount).isEqualTo(expectedDiscount);
    }
}
