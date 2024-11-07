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
            String line;
            reader.readLine(); // 헤더 라인 무시
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String name = fields[0];
                int buyQuantity = Integer.parseInt(fields[1]);
                int freeQuantity = Integer.parseInt(fields[2]);
                LocalDate startDate = LocalDate.parse(fields[3]);
                LocalDate endDate = LocalDate.parse(fields[4]);

                Promotion promotion = new Promotion(name, buyQuantity, freeQuantity, startDate, endDate);

                // 프로모션이 현재 유효하지 않은 경우 null로 처리하여 promotions에 추가
                if (!promotion.isValid(currentDate)) {
                    promotions.put(name, null); // 만료된 프로모션은 null로 등록
                } else {
                    promotions.put(name, promotion);
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] 프로모션 파일을 불러오는 중 오류가 발생했습니다.");
        }
    }

    private void loadProducts(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // 헤더 라인 무시
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String name = fields[0];
                int price = Integer.parseInt(fields[1]);
                int stock = Integer.parseInt(fields[2]);
                String promotionName = fields.length > 3 ? fields[3] : null;
                Promotion promotion = promotions.get(promotionName);

                // 중복된 상품을 찾고, 프로모션이 같고 상품명이 같다면 재고를 합산
                Optional<Product> existingProduct = findProductByNameAndPromotion(name, promotion);
                if (existingProduct.isPresent()) {
                    existingProduct.get().addStock(stock);
                } else {
                    products.add(new Product(name, price, stock, promotion));
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] 상품 파일을 불러오는 중 오류가 발생했습니다.");
        }
    }

    private Optional<Product> findProductByNameAndPromotion(String name, Promotion promotion) {
        return products.stream()
                .filter(product -> product.getName().equals(name) &&
                        ((product.getPromotion() == null && promotion == null) ||
                                (product.getPromotion() != null && product.getPromotion().equals(promotion))))
                .findFirst();
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public Optional<Product> getProductByNameAndPromotion(String productName, boolean hasPromotion) {
        return products.stream()
                .filter(product -> product.getName().equalsIgnoreCase(productName) &&
                        ((hasPromotion && product.getPromotion() != null) ||
                                (!hasPromotion && product.getPromotion() == null)))
                .findFirst();
    }

    public void reduceStock(String productName, int promoQuantity, int regularQuantity) {
        Product promoProduct = getProductByNameAndPromotion(productName, true).orElse(null);
        Product regularProduct = getProductByNameAndPromotion(productName, false).orElse(null);

        int remainingPromoQuantity = promoQuantity;
        if (promoProduct != null && remainingPromoQuantity > 0) {
            int promoStockUsed = Math.min(remainingPromoQuantity, promoProduct.getStock());
            promoProduct.reduceStock(promoStockUsed, 0);
            remainingPromoQuantity -= promoStockUsed;
        }

        if (remainingPromoQuantity > 0 && regularProduct != null) {
            regularProduct.reduceStock(0, remainingPromoQuantity);
        }

        if (regularQuantity > 0 && regularProduct != null) {
            regularProduct.reduceStock(0, regularQuantity);
        }
    }
}
