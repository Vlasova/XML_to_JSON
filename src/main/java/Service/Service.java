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

/**
 * The class starts http server on specified port, waits and processes POST requests
 */
public class Service {
    private static final String PATH = "/json";
    private static final String BAD_REQUEST_MESSAGE = "Bad request";

    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;

    private final HttpServer server;

    /**
     * Method creates http server and handle for processing POST requests
     * @param port port number
     * @throws IOException if http server cannot be created
     */
    public Service(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(PATH, createHandler());
    }

    /**
     * Method starts http server
     */
    public void start() {
        server.start();
    }

    /**
     * Method sends response to client
     * @param http response attributes
     * @param code response code
     * @param response content of response
     * @throws IOException if response cannot be sent
     */
    private void sendResponse(HttpExchange http, int code, byte[] response) throws IOException {
        http.sendResponseHeaders(code, response.length);
        http.getResponseBody().write(response);
        http.close();
    }

    /**
     * Method creates handler of incoming POST requests
     * @return handler
     */
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


    /**
     * Method converts xml string to json format
     * @param xml string in xml format
     * @return string in json format
     * @throws JSONException if xml string is incorrect
     */
    private String convertToJson(String xml) throws JSONException {
        return XML.toJSONObject(xml).toString(4);
    }

    /**
     * Method saves data from InputStream to string
     * @param is Input Stream data
     * @return data string
     * @throws IOException if data cannot be read
     */
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
