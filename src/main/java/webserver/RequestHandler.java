package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import model.Status;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private final Map<String, String> requestInfo = new HashMap<>();
    private final UserService userService;

    private StringBuilder responseHeaderBuilder = new StringBuilder();
    private byte[] responseBody;
    private Status status;

    public RequestHandler(Socket connectionSocket, UserService userService) {
        this.connection = connectionSocket;
        this.userService = userService;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            setRequestInfo(in);

            byte[] responseBody = null;
            switch(this.requestInfo.get("method")) {
                case "GET":
                    handleGet();
                    break;
                case "POST":
                    handlePost();
                    break;
                default:
                    throw new IllegalStateException("Wrong Request Method");
            }

            DataOutputStream dos = new DataOutputStream(out);
            writeResponseHeader(dos);
            writeResponseBody(dos);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void writeResponseHeader(DataOutputStream dos) {
        try {
            dos.writeBytes(responseHeaderBuilder.toString());
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void writeResponseBody(DataOutputStream dos) {
        try {
            dos.write(responseBody, 0, responseBody.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void setRequestInfo(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line = br.readLine();
        log.debug(line);

        String[] methodUriProtocol = line.split(" ");
        // 요청 정보
        this.requestInfo.put("method", methodUriProtocol[0]);
        this.requestInfo.put("uri", methodUriProtocol[1]);
        this.requestInfo.put("protocol", methodUriProtocol[2]);

        // 요청 헤더
        while (!"".equals(line = br.readLine())) {
            if (line == null) {
                throw new IllegalStateException("Wrong Request String");
            }

            log.debug(line);

            int idx = line.indexOf(":");
            this.requestInfo.put(line.substring(0, idx), line.substring(idx + 2));
        }

        // 요청 body
        if (requestInfo.get("Content-Length") != null) {
            int contentLength = Integer.parseInt(requestInfo.get("Content-Length"));
            String body = IOUtils.readData(br, contentLength);
            log.debug(body);
            this.requestInfo.put("body", body);
        }
    }

    private void handleGet() throws IOException {
        String requestPath = getRequestPath();

        switch(requestPath) {
            case "/create":
                setUserInfoFromQueryString(getQueryString());
            default:
                this.responseBody = Files.readAllBytes(new File("./webapp" + (isRootPath(requestPath) ? "/index.html" : requestPath)).toPath());
                responseHeaderBuilder.append("HTTP/1.1 200 OK\r\n");
                responseHeaderBuilder.append(
                        "Content-Type: text/html;charset=utf-8\r\n" +
                                "Content-Length: ");
                responseHeaderBuilder.append(this.responseBody.length);
        }
    }

    private byte[] handlePost() throws IOException {
        String requestPath = getRequestPath();
        switch(requestPath) {
            case "/user/create":
                setUserInfoFromBody().ifPresentOrElse(
                        (user) -> userService.saveUser(user),
                        () -> { throw new IllegalArgumentException("Wrong Query String Parameters."); }
                );
            case "/user/login":
                setUserInfoFromBody().ifPresentOrElse(
                        (user) -> {
                            boolean login = userService.login(user);
                            responseHeaderBuilder.append(
                                    "HTTP/1.1 302 OK\r\n" +
                                    "Location: ");
                            responseHeaderBuilder.append(login ? "/index.html\r\n" : "/user/login_failed.html\r\n");
                            responseHeaderBuilder.append("\r\nSet-Cookie: logined=");
                            responseHeaderBuilder.append(login ? "true\r\n" : "false\r\n");
                        },
                        () -> { throw new IllegalArgumentException("Wrong Query String Parameters."); }
                );
                return "".getBytes(StandardCharsets.UTF_8);
            default:
                return "Failure".getBytes(StandardCharsets.UTF_8);
        }
    }

    private boolean isRootPath(String path) {
        return "".equals(path.replace("/", ""));
    }

    private String getRequestPath() {
        String uriText = this.requestInfo.get("uri");
        int splitPoint = uriText.indexOf('?');
        return splitPoint == -1 ? uriText : uriText.substring(0, splitPoint);
    }

    private String getQueryString() {
        String uriText = this.requestInfo.get("uri");
        int splitPoint = uriText.indexOf('?');
        return splitPoint == -1 ? null : uriText.substring(splitPoint + 1);
    }

    private Optional<User> setUserInfoFromQueryString(String queryString) {
        User user = null;
        if (queryString != null) {
            Map<String, String> parsedMap = parseQueryString(queryString);
            user = new User(parsedMap.get("userId"), parsedMap.get("password"), parsedMap.get("name"), parsedMap.get("email"));
        }
        log.info("setUserInfo : {}", user);

        return Optional.ofNullable(user);
    }

    private Map<String, String> parseQueryString(String queryString) {
        String[] paramPairs = queryString.split("&");
        HashMap<String, String> parsedMap = new HashMap<>();

        for (String pair : paramPairs) {
            String[] tokens = pair.split("=");
            parsedMap.put(tokens[0], tokens[1]);
        }

        return parsedMap;
    }

    private Optional<User> setUserInfoFromBody() {
        if ("application/x-www-form-urlencoded".equals(this.requestInfo.get("Content-Type"))) {
            return setUserInfoFromQueryString(this.requestInfo.get("body"));
        }

        return Optional.empty();
    }
}
