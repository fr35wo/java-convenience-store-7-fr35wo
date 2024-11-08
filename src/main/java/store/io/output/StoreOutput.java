package store.io.output;

import java.util.List;
import store.domain.Product;
import store.domain.Receipt;

public interface StoreOutput {
    void printProductList(List<Product> products);

    void printReceipt(Receipt receipt);

    void printError(String message);

    void printThankYouMessage();

    void close();
}
