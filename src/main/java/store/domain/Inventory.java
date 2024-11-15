package store.domain;

import camp.nextstep.edu.missionutils.DateTimes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import store.common.ErrorMessages;
import store.io.output.StoreOutput;

public class Inventory {
    private static final String PROMOTIONS_FILE_PATH = "src/main/resources/promotions.md";
    private static final String PRODUCTS_FILE_PATH = "src/main/resources/products.md";
    public static final String MD_FILE_DELIMITER = ",";

    private static final int NAME_INDEX = 0;
    private static final int BUY_QUANTITY_INDEX = 1;
    private static final int FREE_QUANTITY_INDEX = 2;
    private static final int START_DATE_INDEX = 3;
    private static final int END_DATE_INDEX = 4;
    private static final int PRICE_INDEX = 1;
    private static final int STOCK_INDEX = 2;
    private static final int PROMOTION_NAME_INDEX = 3;

    private static final int DEFAULT_STOCK = 0;
    private static final int EMPTY_PROMOTION = 0;

    private final List<Product> products = new ArrayList<>();
    private final Map<String, Promotion> promotions = new HashMap<>();

    public Inventory() {
        loadPromotions();
        loadProducts();
    }

    private void loadPromotions() {
        LocalDate currentDate = DateTimes.now().toLocalDate();
        try (BufferedReader reader = new BufferedReader(new FileReader(PROMOTIONS_FILE_PATH))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                Promotion promotion = parsePromotion(line);
                addPromotionToMap(promotion, currentDate);
            }
        } catch (IOException e) {
            System.out.println(ErrorMessages.ERROR_LOADING_PROMOTIONS_FILE);
        }
    }

    private Promotion parsePromotion(String line) {
        String[] fields = line.split(MD_FILE_DELIMITER);
        String name = fields[NAME_INDEX];
        int buyQuantity = Integer.parseInt(fields[BUY_QUANTITY_INDEX]);
        int freeQuantity = Integer.parseInt(fields[FREE_QUANTITY_INDEX]);
        LocalDate startDate = LocalDate.parse(fields[START_DATE_INDEX]);
        LocalDate endDate = LocalDate.parse(fields[END_DATE_INDEX]);
        return new Promotion(name, buyQuantity, freeQuantity, startDate, endDate);
    }

    private void addPromotionToMap(Promotion promotion, LocalDate currentDate) {
        if (!promotion.isValid(currentDate)) {
            promotions.put(promotion.getName(), null);
            return;
        }
        promotions.put(promotion.getName(), promotion);
    }

    private void loadProducts() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE_PATH))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                Product product = parseProduct(line);
                addProductToInventory(product);
            }
        } catch (IOException e) {
            System.out.println(ErrorMessages.ERROR_LOADING_PRODUCTS_FILE);
        }
    }

    private Product parseProduct(String line) {
        String[] fields = line.split(MD_FILE_DELIMITER);
        String name = fields[NAME_INDEX];
        int price = Integer.parseInt(fields[PRICE_INDEX]);
        int stock = Integer.parseInt(fields[STOCK_INDEX]);

        String promotionName = null;
        if (fields.length > PROMOTION_NAME_INDEX) {
            promotionName = fields[PROMOTION_NAME_INDEX];
        }

        Promotion promotion = promotions.get(promotionName);
        return new Product(name, price, stock, promotion);
    }

    private void addProductToInventory(Product product) {
        Optional<Product> existingProduct = findProductByNameAndPromotion(product.getName(), product.getPromotion());
        if (existingProduct.isEmpty()) {
            products.add(product);
            return;
        }
        existingProduct.get().addStock(product.getStock());
    }

    private Optional<Product> findProductByNameAndPromotion(String name, Promotion promotion) {
        return products.stream()
                .filter(product -> isMatchingProduct(product, name, promotion))
                .findFirst();
    }

    private boolean isMatchingProduct(Product product, String name, Promotion promotion) {
        if (!product.getName().equals(name)) {
            return false;
        }
        return isMatchingPromotion(product, promotion);
    }

    private boolean isMatchingPromotion(Product product, Promotion promotion) {
        if (product.getPromotion() == null && promotion == null) {
            return true;
        }
        if (product.getPromotion() != null) {
            return product.getPromotion().equals(promotion);
        }
        return false;
    }

    public Optional<Product> getProductByNameAndPromotion(String productName, boolean hasPromotion) {
        return products.stream()
                .filter(product -> isMatchingProductByNameAndPromotion(product, productName, hasPromotion))
                .findFirst();
    }

    private boolean isMatchingProductByNameAndPromotion(Product product, String productName, boolean hasPromotion) {
        if (!product.getName().equalsIgnoreCase(productName)) {
            return false;
        }
        if (hasPromotion) {
            return product.getPromotion() != null;
        }
        return product.getPromotion() == null;
    }

    public void updateInventory(Cart cart) {
        validateStock(cart);
        reduceStock(cart);
    }

    private void validateStock(Cart cart) {
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            int requiredQuantity = item.getQuantity();
            int totalAvailableStock = getTotalAvailableStock(product);

            if (requiredQuantity > totalAvailableStock) {
                throw new IllegalStateException(ErrorMessages.EXCEED_STOCK);
            }
        }
    }

    private int getTotalAvailableStock(Product product) {
        int availablePromoStock = getAvailablePromoStock(product);
        int availableRegularStock = getAvailableRegularStock(product);
        return availablePromoStock + availableRegularStock;
    }

    private int getAvailablePromoStock(Product product) {
        Optional<Product> promoProduct = getProductByNameAndPromotion(product.getName(), true);
        if (promoProduct.isPresent()) {
            return promoProduct.get().getStock();
        }
        return DEFAULT_STOCK;
    }

    private int getAvailableRegularStock(Product product) {
        Optional<Product> regularProduct = getProductByNameAndPromotion(product.getName(), false);
        if (regularProduct.isPresent()) {
            return regularProduct.get().getStock();
        }
        return DEFAULT_STOCK;
    }

    private void reduceStock(Cart cart) {
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            int requiredQuantity = item.getQuantity();

            int promoQuantity = calculatePromoQuantity(product, requiredQuantity);
            int regularQuantity = requiredQuantity - promoQuantity;

            reduceStock(product.getName(), promoQuantity, regularQuantity);
        }
    }

    private int calculatePromoQuantity(Product product, int requiredQuantity) {
        if (product.getPromotion() == null) {
            return EMPTY_PROMOTION;
        }
        if (!product.getPromotion().isValid(DateTimes.now().toLocalDate())) {
            return EMPTY_PROMOTION;
        }
        return Math.min(requiredQuantity, product.getStock());
    }

    private void reduceStock(String productName, int promoQuantity, int regularQuantity) {
        reducePromotionStock(productName, promoQuantity);
        reduceRegularStock(productName, regularQuantity);
    }

    private void reducePromotionStock(String productName, int promoQuantity) {
        if (promoQuantity <= DEFAULT_STOCK) {
            return;
        }
        Optional<Product> promoProduct = getProductByNameAndPromotion(productName, true);
        if (promoProduct.isPresent()) {
            promoProduct.get().reducePromotionStock(promoQuantity);
        }
    }

    private void reduceRegularStock(String productName, int regularQuantity) {
        if (regularQuantity <= DEFAULT_STOCK) {
            return;
        }
        Optional<Product> regularProduct = getProductByNameAndPromotion(productName, false);
        if (regularProduct.isPresent()) {
            regularProduct.get().reduceRegularStock(regularQuantity);
        }
    }

    public void printProductList(StoreOutput storeOutput) {
        storeOutput.printProductList(products);
    }
}
