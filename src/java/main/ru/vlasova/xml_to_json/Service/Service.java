package ru.vlasova.xml_to_json.Service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.XML;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class Service {
    private static final String PATH = "/convert";
    private static final String BAD_REQUEST_MESSAGE = "Bad request";

    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;

    @NotNull
    private final HttpServer server;

    public Service(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(PATH, createHandler());
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private void sendResponse(@NotNull HttpExchange http, int code, byte[] response) throws IOException {
        http.sendResponseHeaders(code, response.length);
        http.getResponseBody().write(response);
        http.close();
    }

    @NotNull
    private HttpHandler createHandler() {
        return http -> {
            if("PUT".equals(http.getRequestMethod())) {
                try {
                    sendResponse(http, OK, convertToJson(readInputStreamData(http.getRequestBody())).getBytes());
                } catch (JSONException e) {
                    sendResponse(http, BAD_REQUEST, BAD_REQUEST_MESSAGE.getBytes());
                }
            }
        };
    }

    @NotNull
    private String convertToJson(@NotNull String xml) throws JSONException {
        return XML.toJSONObject(xml).toString(4);
    }

    @NotNull
    private String readInputStreamData(@NotNull InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count;
        while ((count = is.read(data)) != -1) {
            baos.write(data, 0, count);
        }
        return new String(baos.toByteArray());
    }
}
