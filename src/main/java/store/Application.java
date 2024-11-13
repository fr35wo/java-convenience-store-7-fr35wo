package store;

import store.config.ConvenienceStoreConfig;
import store.controller.ConvenienceStoreController;

public class Application {
    public static void main(String[] args) {
        ConvenienceStoreConfig convenienceStoreConfig = new ConvenienceStoreConfig();
        ConvenienceStoreController controller = convenienceStoreConfig.convenienceStoreController();
        controller.start();
    }
}
