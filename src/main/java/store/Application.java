package store;

import store.controller.ConvenienceStoreController;
import store.domain.Inventory;
import store.io.input.impl.InputView;
import store.io.output.impl.OutputView;
import store.service.ConvenienceStoreService;

public class Application {
    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();

        Inventory inventory = new Inventory();
        ConvenienceStoreService convenienceStoreService = new ConvenienceStoreService();

        ConvenienceStoreController controller = new ConvenienceStoreController(inputView, outputView, inventory,
                convenienceStoreService);

        controller.start();
    }
}
