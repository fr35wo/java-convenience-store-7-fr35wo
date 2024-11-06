package store;

import store.controller.ConvenienceStoreController;
import store.domain.Inventory;
import store.io.input.impl.InputView;
import store.io.output.impl.OutputView;

public class Application {
    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();

        Inventory inventory = new Inventory();

        ConvenienceStoreController controller = new ConvenienceStoreController(inputView, outputView, inventory);
        
        controller.start();
    }
}
