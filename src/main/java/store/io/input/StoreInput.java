package store.io.input;

public interface StoreInput {
    String getPurchaseItemsInput();

    boolean askForAdditionalPromo(String itemName, int additionalCount);

    boolean askForFullPricePurchase(String itemName, int shortageCount);

    boolean askForMembershipDiscount();

    boolean askForAdditionalPurchase();
}
