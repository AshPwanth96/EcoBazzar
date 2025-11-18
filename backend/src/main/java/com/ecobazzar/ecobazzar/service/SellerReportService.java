package com.ecobazzar.ecobazzar.service;

import java.util.*;
import org.springframework.stereotype.Service;
import com.ecobazzar.ecobazzar.dto.SellerReport;
import com.ecobazzar.ecobazzar.model.Product;
import com.ecobazzar.ecobazzar.model.User;
import com.ecobazzar.ecobazzar.repository.OrderItemRepository;
import com.ecobazzar.ecobazzar.repository.ProductRepository;
import com.ecobazzar.ecobazzar.repository.UserRepository;

@Service
public class SellerReportService {

    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public SellerReportService(OrderItemRepository orderItemRepository,
                               UserRepository userRepository,
                               ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public SellerReport getSellerReport(String email) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Seller Not Found"));
        Long sellerId = seller.getId();
        List<Product> sellerProducts = productRepository.findBySellerId(sellerId);
        long totalProducts = sellerProducts.size();
        long totalEcoCertified = sellerProducts.stream()
                .filter(p -> Boolean.TRUE.equals(p.getEcoCertified()))
                .count();
        Double totalRevenue = orderItemRepository.getTotalRevenueBySeller(sellerId);
        if (totalRevenue == null) totalRevenue = 0.0;
        Long totalOrders = (long) orderItemRepository.findBySellerId(sellerId).size();
        String badge = getSellerBadge(totalRevenue, totalEcoCertified);
        return new SellerReport(
                sellerId,
                seller.getName(),
                totalProducts,
                totalEcoCertified,
                totalOrders,
                totalRevenue,
                badge
        );
    }

    public List<Map<String, Object>> getSellerSales(String email, int days) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Seller Not Found"));
        Long sellerId = seller.getId();
        List<Object[]> rows = orderItemRepository.getDailyRevenueBySeller(sellerId, days);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object[] r : rows) {
            Map<String, Object> m = new HashMap<>();
            m.put("day", String.valueOf(r[0]));
            m.put("revenue", r[1] == null ? 0.0 : ((Number) r[1]).doubleValue());
            out.add(m);
        }
        return out;
    }

    private String getSellerBadge(Double revenue, Long totalEcoCertified) {
        if (revenue > 100000.0 && totalEcoCertified > 20) return "🏆 Top Seller";
        if (revenue > 50000.0) return "🚀 Growing Seller";
        if (totalEcoCertified > 10) return "🌿 Trusted Eco Seller";
        return "📈 New Seller";
    }
}
