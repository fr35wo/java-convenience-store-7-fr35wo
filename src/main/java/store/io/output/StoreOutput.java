package store.io.output;

import java.util.List;
import store.domain.Cart;
import store.domain.Membership;
import store.domain.Product;
import store.domain.Receipt;

public interface StoreOutput {
    void printProductList(List<Product> products);

    void printReceipt(Receipt receipt, Cart cart, Membership membership);

    void printError(String message);

    void close();
}
