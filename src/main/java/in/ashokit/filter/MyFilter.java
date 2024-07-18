package in.ashokit.filter;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class MyFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("Filter executed...");

        // Get the incoming HTTP request
        ServerHttpRequest request = exchange.getRequest();

        // Retrieve headers from the request
        HttpHeaders headers = request.getHeaders();
        Set<String> keySet = headers.keySet();

        if (!keySet.contains("token")) {
            return handleUnauthorized(exchange, "Missing token");
        }

        List<String> list = headers.get("token");

        if (list == null || list.isEmpty() || !list.get(0).equals("akhil@123")) {
            return handleUnauthorized(exchange, "Invalid token");
        }

        System.out.println("-----------------------");

        // Continue the filter chain
        return chain.filter(exchange);
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "text/plain");
        byte[] bytes = message.getBytes();
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}
