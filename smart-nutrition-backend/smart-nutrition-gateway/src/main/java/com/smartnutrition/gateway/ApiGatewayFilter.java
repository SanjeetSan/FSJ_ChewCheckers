package com.smartnutrition.gateway;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

@Component
public class ApiGatewayFilter extends OncePerRequestFilter {

    private final GatewayMetricsService gatewayMetricsService;

    public ApiGatewayFilter(GatewayMetricsService gatewayMetricsService) {
        this.gatewayMetricsService = gatewayMetricsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Standard CORS headers
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin") != null ? request.getHeader("Origin") : "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Handle Gateway internal management endpoints
        if (path.startsWith("/api/gateway") || path.startsWith("/actuator") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String targetBaseUrl = resolveTargetBaseUrl(path);
        if (targetBaseUrl == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String routeName = resolveMicroserviceRoute(path);
        gatewayMetricsService.recordRequest(routeName);

        proxyRequest(request, response, targetBaseUrl, routeName);
    }

    private String resolveTargetBaseUrl(String path) {
        if (path.startsWith("/api/auth") || path.startsWith("/api/admin") || path.startsWith("/api/users") || path.startsWith("/uploads")) {
            return "http://localhost:8081";
        } else if (path.startsWith("/api/meals") || path.startsWith("/api/assistant")) {
            return "http://localhost:8082";
        } else if (path.startsWith("/api/teacher") || path.startsWith("/api/parent") || path.startsWith("/api/reports")) {
            return "http://localhost:8083";
        } else if (path.startsWith("/api/messages") || path.startsWith("/api/social")) {
            return "http://localhost:8084";
        }
        return null;
    }

    private String resolveMicroserviceRoute(String path) {
        if (path.startsWith("/api/auth") || path.startsWith("/api/admin") || path.startsWith("/api/users") || path.startsWith("/uploads")) {
            return "AUTH-ADMIN-MICROSERVICE";
        } else if (path.startsWith("/api/meals") || path.startsWith("/api/assistant")) {
            return "AI-MEAL-VISION-MICROSERVICE";
        } else if (path.startsWith("/api/teacher") || path.startsWith("/api/parent") || path.startsWith("/api/reports")) {
            return "SCHOOL-REPORT-MICROSERVICE";
        } else if (path.startsWith("/api/messages") || path.startsWith("/api/social")) {
            return "MESSAGING-SOCIAL-MICROSERVICE";
        }
        return "GENERAL-GATEWAY-ROUTE";
    }

    private void proxyRequest(HttpServletRequest request, HttpServletResponse response, String targetBaseUrl, String routeName) throws IOException {
        String queryString = request.getQueryString();
        String targetUrlStr = targetBaseUrl + request.getRequestURI() + (queryString != null ? "?" + queryString : "");
        URL targetUrl = new URL(targetUrlStr);

        HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();
        conn.setRequestMethod(request.getMethod());
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(15000);
        conn.setDoInput(true);

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!headerName.equalsIgnoreCase("host") && !headerName.equalsIgnoreCase("content-length")) {
                Enumeration<String> headers = request.getHeaders(headerName);
                while (headers.hasMoreElements()) {
                    conn.addRequestProperty(headerName, headers.nextElement());
                }
            }
        }

        if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod()) || "PATCH".equalsIgnoreCase(request.getMethod())) {
            conn.setDoOutput(true);
            byte[] bodyBytes = request.getInputStream().readAllBytes();
            if (bodyBytes.length > 0) {
                conn.setFixedLengthStreamingMode(bodyBytes.length);
                try (OutputStream out = conn.getOutputStream()) {
                    out.write(bodyBytes);
                    out.flush();
                }
            }
        }

        int responseCode;
        try {
            responseCode = conn.getResponseCode();
        } catch (IOException e) {
            responseCode = HttpServletResponse.SC_BAD_GATEWAY;
        }

        response.setStatus(responseCode);

        conn.getHeaderFields().forEach((key, values) -> {
            if (key != null && !key.equalsIgnoreCase("Transfer-Encoding")) {
                for (String value : values) {
                    response.addHeader(key, value);
                }
            }
        });

        response.setHeader("X-Gateway-Router", "SmartNutrition-InProject-Gateway-v1");
        response.setHeader("X-Microservice-Route", routeName);

        if (responseCode != HttpServletResponse.SC_NO_CONTENT && responseCode != HttpServletResponse.SC_RESET_CONTENT) {
            try {
                InputStream responseStream = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
                if (responseStream != null) {
                    try (InputStream in = responseStream; OutputStream out = response.getOutputStream()) {
                        in.transferTo(out);
                    }
                }
            } catch(IOException ignored) {}
        }
    }
}

