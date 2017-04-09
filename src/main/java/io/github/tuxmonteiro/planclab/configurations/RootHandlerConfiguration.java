package io.github.tuxmonteiro.planclab.configurations;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.NameVirtualHostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootHandlerConfiguration {

    private static final int SERVICE_UNAVAILABLE = 503;

    private final NameVirtualHostHandler nameVirtualHostHandler;

    @Autowired
    public RootHandlerConfiguration(final NameVirtualHostHandler nameVirtualHostHandler) {
        this.nameVirtualHostHandler = nameVirtualHostHandler;
    }

    @Bean("rootHandler")
    public HttpHandler rootHandler() {
        return new HttpHandler() {

            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                try {
                    nameVirtualHostHandler.handleRequest(exchange);
                } catch (Exception e) {
                    serviceUnavailableHandler().handleRequest(exchange);
                }
            }

            private HttpHandler serviceUnavailableHandler() {
                return exchange -> exchange.setStatusCode(SERVICE_UNAVAILABLE);
            }
        };
    }
}
