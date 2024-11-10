package store.domain;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;

public class Promotion {
    private static final int DEFAULT_FREE_QUANTITY = 0;

    private final String name;
    private final int buyQuantity;
    private final int freeQuantity;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Promotion(String name, int buyQuantity, int freeQuantity, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.buyQuantity = buyQuantity;
        this.freeQuantity = freeQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int calculateFreeItems(int purchasedQuantity, int stock) {
        if (isValid(DateTimes.now().toLocalDate())) {
            int totalRequired = buyQuantity + freeQuantity;
            int promoSets = Math.min(purchasedQuantity / totalRequired, stock / totalRequired);
            return promoSets * freeQuantity;
        }
        return DEFAULT_FREE_QUANTITY;
    }

    public boolean isValid(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public String getName() {
        return name;
    }

    public int getBuyQuantity() {
        return buyQuantity;
    }

    public int getFreeQuantity() {
        return freeQuantity;
    }
}