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
import store.io.output.StoreOutput;

public class Inventory {
    private final List<Product> products = new ArrayList<>();
    private final Map<String, Promotion> promotions = new HashMap<>();

    public Inventory() {
        loadPromotions("src/main/resources/promotions.md");
        loadProducts("src/main/resources/products.md");
    }

    private void loadPromotions(String filePath) {
        LocalDate currentDate = DateTimes.now().toLocalDate();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                Promotion promotion = parsePromotion(line, currentDate);
                addPromotionToMap(promotion, currentDate);
            }
        } catch (IOException e) {
            System.out.println("프로모션 파일을 불러오는 중 오류가 발생했습니다.");
        }
    }

    private Promotion parsePromotion(String line, LocalDate currentDate) {
        String[] fields = line.split(",");
        String name = fields[0];
        int buyQuantity = Integer.parseInt(fields[1]);
        int freeQuantity = Integer.parseInt(fields[2]);
        LocalDate startDate = LocalDate.parse(fields[3]);
        LocalDate endDate = LocalDate.parse(fields[4]);
        return new Promotion(name, buyQuantity, freeQuantity, startDate, endDate);
    }

    private void addPromotionToMap(Promotion promotion, LocalDate currentDate) {
        if (promotion.isValid(currentDate)) {
            promotions.put(promotion.getName(), promotion);
        } else {
            promotions.put(promotion.getName(), null);
        }
    }

    private void loadProducts(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                Product product = parseProduct(line);
                addProductToInventory(product);
            }
        } catch (IOException e) {
            System.out.println("상품 파일을 불러오는 중 오류가 발생했습니다.");
        }
    }

    private Product parseProduct(String line) {
        String[] fields = line.split(",");
        String name = fields[0];
        int price = Integer.parseInt(fields[1]);
        int stock = Integer.parseInt(fields[2]);
        String promotionName = fields.length > 3 ? fields[3] : null;
        Promotion promotion = promotions.get(promotionName);
        return new Product(name, price, stock, promotion);
    }

    private void addProductToInventory(Product product) {
        Optional<Product> existingProduct = findProductByNameAndPromotion(product.getName(), product.getPromotion());
        if (existingProduct.isPresent()) {
            existingProduct.get().addStock(product.getStock());
            return;
        }
        products.add(product);
    }

    private Optional<Product> findProductByNameAndPromotion(String name, Promotion promotion) {
        return products.stream()
                .filter(product -> isMatchingProduct(product, name, promotion))
                .findFirst();
    }

    private boolean isMatchingProduct(Product product, String name, Promotion promotion) {
        return product.getName().equals(name) && isMatchingPromotion(product, promotion);
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
                throw new IllegalStateException("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
            }
        }
    }

    private int getTotalAvailableStock(Product product) {
        int availablePromoStock = getAvailablePromoStock(product);
        int availableRegularStock = getAvailableRegularStock(product);
        return availablePromoStock + availableRegularStock;
    }

    private int getAvailablePromoStock(Product product) {
        return getProductByNameAndPromotion(product.getName(), true)
                .map(Product::getStock)
                .orElse(0);
    }

    private int getAvailableRegularStock(Product product) {
        return getProductByNameAndPromotion(product.getName(), false)
                .map(Product::getStock)
                .orElse(0);
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
            return 0;
        }
        if (!product.getPromotion().isValid(LocalDate.now())) {
            return 0;
        }
        return Math.min(requiredQuantity, product.getStock());
    }

    private void reduceStock(String productName, int promoQuantity, int regularQuantity) {
        reducePromotionStock(productName, promoQuantity);
        reduceRegularStock(productName, regularQuantity);
    }

    private void reducePromotionStock(String productName, int promoQuantity) {
        if (promoQuantity <= 0) {
            return;
        }
        getProductByNameAndPromotion(productName, true)
                .ifPresent(product -> product.reducePromotionStock(promoQuantity));
    }

    private void reduceRegularStock(String productName, int regularQuantity) {
        if (regularQuantity <= 0) {
            return;
        }
        getProductByNameAndPromotion(productName, false)
                .ifPresent(product -> product.reduceRegularStock(regularQuantity));
    }

    public void printProductList(StoreOutput storeOutput) {
        storeOutput.printProductList(products);
    }
}
