package store.config;

import store.controller.ConvenienceStoreController;
import store.io.input.StoreInput;
import store.io.input.impl.InputConsole;
import store.io.output.StoreOutput;
import store.io.output.impl.OutputConsole;
import store.service.ConvenienceStoreService;

public class ConvenienceStoreConfig {
    public StoreInput storeInput() {
        return new InputConsole();
    }

    public StoreOutput storeOutput() {
        return new OutputConsole();
    }

    public ConvenienceStoreService convenienceStoreService() {
        return new ConvenienceStoreService();
    }

    public ConvenienceStoreController convenienceStoreController() {
        return new ConvenienceStoreController(storeInput(), storeOutput(), convenienceStoreService());
    }
}

