/**
 *
 *
 */
package io.github.tuxmonteiro.planclab.handlers;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.util.Headers;
import jodd.util.Wildcard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathGlobHandler implements HttpHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ConcurrentMap<PathOrdered, HttpHandler> paths = new ConcurrentSkipListMap<>();

    private HttpHandler defaultHandler = ResponseCodeHandler.HANDLE_500;

    public PathGlobHandler(HttpHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    private HttpHandler pathGlobHandlerCheck() {
        return exchange -> {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseHeaders().put(Headers.SERVER, "PLANC");
            exchange.getResponseSender().send("1");
        };
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (paths.isEmpty()) {
            defaultHandler.handleRequest(exchange);
            return;
        }
        final String path = exchange.getRelativePath();
        if (path.equals("/__path_handler_check__")) {
            pathGlobHandlerCheck().handleRequest(exchange);
            return;
        }

        AtomicBoolean hit = new AtomicBoolean(false);
        paths.forEach((key, handler) -> {
            if (!hit.get()) {
                final String pathKey = key.getPath();
                hit.set(Wildcard.match(path, pathKey));
                if (hit.get()) {
                    try {
                        if (handler != null) {
                            handler.handleRequest(exchange);
                        } else {
                            logger.error("Handler is null");
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        });
        if (!hit.get()) {
            defaultHandler.handleRequest(exchange);
        }
    }

    public synchronized boolean contains(final String path) {
        return paths.containsKey(new PathOrdered(path, 0));
    }

    public synchronized boolean addPath(final String path, int order, final HttpHandler handler) {
        return paths.put(new PathOrdered(path.endsWith("/") && !path.contains("*")? path + "*" : path, order), handler) == null;
    }

    public synchronized boolean removePath(final String path) {
        return paths.remove(new PathOrdered(path, 0)) == null;
    }

    public HttpHandler setDefaultHandler(HttpHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
        return this;
    }

    public HttpHandler getDefaultHandler() {
        return this.defaultHandler;
    }

    public synchronized void clear() {
        paths.clear();
    }

    private static class PathOrdered implements Comparable<PathOrdered> {
        private final String path;
        private final int order;

        PathOrdered(String path, int order) {
            this.path = path;
            this.order = order;
        }

        String getPath() {
            return path;
        }

        public int getOrder() {
            return order;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathOrdered that = (PathOrdered) o;
            return Objects.equals(path, that.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path);
        }

        @Override
        public int compareTo(final PathOrdered other) {
            if (other == null) return 1;
            return this.order < other.order ? -1 : this.order > other.order ? 1 : 0;
        }
    }
}
