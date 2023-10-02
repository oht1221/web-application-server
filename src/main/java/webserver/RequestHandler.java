package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import model.Status;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private final Map<String, String> requestInfo = new HashMap<>();
    private final Map<String, String> members;

    public RequestHandler(Socket connectionSocket, Map<String, String> members) {
        this.connection = connectionSocket;
        this.members = members;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            setRequestInfo(in);

            byte[] responseBody = null;
            Status statusCode = Status.OK;
            switch(this.requestInfo.get("method")) {
                case "GET":
                    responseBody = handleGet();
                    statusCode = Status.OK;
                    break;
                case "POST":
                    responseBody = handlePost();
                    statusCode = Status.REDIRECT;
                    break;
                default:
                    throw new IllegalStateException("Wrong Request Method");
            }

            DataOutputStream dos = new DataOutputStream(out);
            responseHeader(dos, responseBody.length, statusCode);
            responseBody(dos, responseBody);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseHeader(DataOutputStream dos, int lengthOfBodyContent, Status status) {
        try {
            dos.writeBytes("HTTP/1.1 " + status.getValue() + " " + status + " \r\n");
            switch(status.getValue()) {
                case "200":
                    dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
                    dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
                    break;
                case "300":
                case "301":
                case "302":
                    dos.writeBytes("Location: /index.html\r\n");
                    break;
                default:
                    throw new IllegalStateException("Wrong HTTP status code");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
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

    private byte[] handleGet() throws IOException {
        String requestPath = getRequestPath();
        switch(requestPath) {
            case "/create":
                setUserInfoFromQueryString(getQueryString());
                return "Success".getBytes(StandardCharsets.UTF_8);
            default:
                return Files.readAllBytes(new File("./webapp" + (isRootPath(requestPath) ? "/index.html" : requestPath)).toPath());
        }
    }

    private byte[] handlePost() throws IOException {
        String requestPath = getRequestPath();
        switch(requestPath) {
            case "/user/create":
                saveUser();
                return "Success".getBytes(StandardCharsets.UTF_8);
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

    private void setUserInfoFromQueryString(String queryString) {
        if (queryString == null) {
            return;
        }

        Map<String, String> parsedMap = parseQueryString(queryString);

        User user = new User(parsedMap.get("userId"), parsedMap.get("password"), parsedMap.get("name"), parsedMap.get("email"));
        log.info("setUserInfo : {}", user);
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

    private void saveUser()
    private void setUserInfoFromBody() {
        if ("application/x-www-form-urlencoded".equals(this.requestInfo.get("Content-Type"))) {
            setUserInfoFromQueryString(this.requestInfo.get("body"));
        }
    }
}
