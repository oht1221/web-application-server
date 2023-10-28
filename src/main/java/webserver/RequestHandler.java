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
    private final Map<String, Controller> CONTROLLER_MAP;

    public RequestHandler(Socket connectionSocket, UserService userService) {
        this.connection = connectionSocket;
        this.CONTROLLER_MAP = Map.of(
                "/user/list", UserListController.create(userService),
                "/user/login", UserLoginController.create(userService),
                "/user/create", UserCreateController.create(userService)
                ,"NOT_FOUND", NotFoundController.create()
        );
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            String path = "/".equals(request.getPath()) || request.getPath().length() == 0 ? "/index.html" : request.getPath();
            // static resources
            if (path.endsWith(".js") || path.endsWith(".css") || path.endsWith(".html")) {
                response.forward(path);
                return;
            }

            Controller controller = CONTROLLER_MAP.get(path) != null ? CONTROLLER_MAP.get(path) : CONTROLLER_MAP.get("NOT_FOUND");
            controller.service(request, response);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
