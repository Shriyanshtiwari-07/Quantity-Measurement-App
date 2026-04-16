package com.app.apigateway.filter;

import com.app.apigateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Centralized JWT authentication filter at the API Gateway.
 *
 * For every request:
 * 1. If the route is public (login, register, OAuth2, Swagger, actuator) → pass through.
 * 2. If an Authorization: Bearer <token> header is present:
 *    a. Validate the JWT.
 *    b. Extract userId, email, and roles from claims.
 *    c. Inject X-User-Id, X-User-Email, X-User-Role headers into the downstream request.
 *    d. Strip the original Authorization header (downstream services trust the X- headers).
 * 3. If no JWT is present on a public route → pass through (anonymous access).
 * 4. If no JWT is present on a protected route → 401 Unauthorized.
 *
 * Downstream services (measurement-service, user-service) simply read the
 * X-User-Id header instead of parsing JWT themselves.
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    /**
     * Paths that do NOT require authentication.
     * Requests to these paths are forwarded even without a JWT.
     */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/forgotPassword",
            "/api/v1/auth/otp",
            "/api/v1/quantities/compare",
            "/api/v1/quantities/convert",
            "/api/v1/quantities/add",
            "/api/v1/quantities/subtract",
            "/api/v1/quantities/divide",
            "/oauth2",
            "/login/oauth2",
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator",
            "/h2-console"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Check if the path is public
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);

        // Extract Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = extractToken(authHeader);

        // If a valid JWT is present, enrich the request with user info headers
        if (token != null && jwtUtil.validateToken(token)) {
            try {
                Claims claims = jwtUtil.getClaims(token);
                String email = claims.getSubject();
                String roles = claims.get("roles", String.class);
                Long userId = null;
                Object userIdClaim = claims.get("userId");
                if (userIdClaim != null) {
                    userId = ((Number) userIdClaim).longValue();
                }

                log.debug("Gateway authenticated: email={}, userId={}, roles={}, path={}",
                        email, userId, roles, path);

                // Build a mutated request with identity headers injected
                ServerHttpRequest.Builder mutatedRequest = request.mutate()
                        .header("X-User-Email", email)
                        .header("X-User-Role", roles != null ? roles : "");

                if (userId != null) {
                    mutatedRequest.header("X-User-Id", String.valueOf(userId));
                }

                // Remove the original Authorization header — downstream trusts X- headers only
                mutatedRequest.headers(h -> h.remove(HttpHeaders.AUTHORIZATION));

                return chain.filter(exchange.mutate().request(mutatedRequest.build()).build());

            } catch (Exception ex) {
                log.warn("Failed to extract claims from JWT: {}", ex.getMessage());
                // Fall through — treat as unauthenticated
            }
        }

        // No valid JWT — allow public paths, reject protected paths
        if (isPublic) {
            return chain.filter(exchange);
        }

        // No JWT on a protected path → 401
        if (token == null) {
            // No token at all — might be anonymous access on paths that
            // optionally accept auth. Let downstream decide.
            // Only hard-reject if the path clearly requires auth.
            if (path.startsWith("/api/v1/quantities/history") ||
                path.startsWith("/api/v1/quantities/count") ||
                path.startsWith("/api/v1/auth/me") ||
                path.startsWith("/api/v1/auth/resetPassword")) {

                log.warn("No JWT for protected path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            // For other paths, let downstream services handle auth
            return chain.filter(exchange);
        }

        // Invalid JWT on a protected path → 401
        log.warn("Invalid JWT for path: {}", path);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        // Run after LoggingFilter (HIGHEST_PRECEDENCE) but before routing
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
