package com.smartnutrition.gateway;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GatewayMetricsService {

    private final AtomicLong totalRoutedRequests = new AtomicLong(0);
    private final Map<String, AtomicLong> microserviceRouteCounts = new ConcurrentHashMap<>();
    private final long startTime = System.currentTimeMillis();

    public void recordRequest(String microserviceRoute) {
        totalRoutedRequests.incrementAndGet();
        microserviceRouteCounts.computeIfAbsent(microserviceRoute, k -> new AtomicLong(0)).incrementAndGet();
    }

    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new ConcurrentHashMap<>();
        metrics.put("status", "UP");
        metrics.put("gatewayName", "SmartNutrition-InProject-Gateway-Router");
        metrics.put("uptimeMs", System.currentTimeMillis() - startTime);
        metrics.put("totalRoutedRequests", totalRoutedRequests.get());
        
        Map<String, Long> routeBreakdown = new ConcurrentHashMap<>();
        microserviceRouteCounts.forEach((key, value) -> routeBreakdown.put(key, value.get()));
        metrics.put("microserviceRouteCounts", routeBreakdown);
        
        return metrics;
    }
}
