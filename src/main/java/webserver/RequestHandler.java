package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

import controller.*;
import model.HttpRequest;
import model.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;

    public RequestHandler(Socket connectionSocket, UserService userService) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            String path = getDefaultPath(request.getPath());

            // static resources
            if (isStatic(path)) {
                response.forward(path);
                return;
            }

            RequestMapping.getController(path).service(request, response);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path) {
        return "/".equals(path) || path.length() == 0 ? "/index.html" : path;
    }

    private boolean isStatic(String path) {
        return path.endsWith(".js") || path.endsWith(".css") || path.endsWith(".html");
    }
}
