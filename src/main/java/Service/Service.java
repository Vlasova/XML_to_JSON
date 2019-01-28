package Service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONException;
import org.json.XML;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class Service {
    private static final String PATH = "/json";
    private static final String BAD_REQUEST_MESSAGE = "Bad request";

    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;

    private final HttpServer server;

    public Service(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(PATH, createHandler());
    }

    public void start() {
        server.start();
    }

    private void sendResponse(HttpExchange http, int code, byte[] response) throws IOException {
        http.sendResponseHeaders(code, response.length);
        http.getResponseBody().write(response);
        http.close();
    }

    private HttpHandler createHandler() {
        return new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if("POST".equals(exchange.getRequestMethod())) {
                    try {
                        sendResponse(exchange, OK, convertToJson(readInputStreamData(exchange.getRequestBody())).getBytes());
                    } catch (JSONException e) {
                        sendResponse(exchange, BAD_REQUEST, BAD_REQUEST_MESSAGE.getBytes());
                    }
                }
            }
        };
    }

    private String convertToJson(String xml) throws JSONException {
        return XML.toJSONObject(xml).toString(4);
    }

    private String readInputStreamData(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count;
        while ((count = is.read(data)) != -1) {
            baos.write(data, 0, count);
        }
        return new String(baos.toByteArray());
    }
}
