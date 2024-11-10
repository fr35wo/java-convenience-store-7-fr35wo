package store.io.input.impl;

import camp.nextstep.edu.missionutils.Console;
import store.common.ConsoleMessages;
import store.common.ErrorMessages;
import store.io.input.StoreInput;


public class InputConsole implements StoreInput {

    @Override
    public String getPurchaseItemsInput() {
        System.out.print(ConsoleMessages.LINE_SEPARATOR);
        System.out.println(ConsoleMessages.PURCHASE_ITEMS_PROMPT);
        return Console.readLine();
    }

    @Override
    public boolean askForAdditionalPromo(String itemName, int additionalCount) {
        System.out.print(ConsoleMessages.LINE_SEPARATOR);
        System.out.printf(ConsoleMessages.ADDITIONAL_PROMO_PROMPT + ConsoleMessages.LINE_SEPARATOR, itemName,
                additionalCount);
        return getYesNoResponse();
    }

    @Override
    public boolean askForFullPricePurchase(String itemName, int shortageCount) {
        System.out.print(ConsoleMessages.LINE_SEPARATOR);
        System.out.printf(ConsoleMessages.FULL_PRICE_PURCHASE_PROMPT + ConsoleMessages.LINE_SEPARATOR, itemName,
                shortageCount);
        return getYesNoResponse();
    }

    @Override
    public boolean askForMembershipDiscount() {
        System.out.print(ConsoleMessages.LINE_SEPARATOR);
        System.out.println(ConsoleMessages.MEMBERSHIP_DISCOUNT_PROMPT);
        return getYesNoResponse();
    }

    @Override
    public boolean askForAdditionalPurchase() {
        System.out.print(ConsoleMessages.LINE_SEPARATOR);
        System.out.println(ConsoleMessages.ADDITIONAL_PURCHASE_PROMPT);
        return getYesNoResponse();
    }

    private boolean getYesNoResponse() {
        while (true) {
            try {
                String input = Console.readLine().trim().toUpperCase();
                if ("Y".equals(input)) {
                    return true;
                }
                if ("N".equals(input)) {
                    return false;
                }
                throw new IllegalArgumentException(ErrorMessages.INVALID_INPUT_MESSAGE);
            } catch (IllegalArgumentException e) {
                System.out.println(ErrorMessages.ERROR_MESSAGE + e.getMessage());
            }
        }
    }
}
