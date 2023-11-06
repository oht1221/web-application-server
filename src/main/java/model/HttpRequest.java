package model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpRequest {
  private RequestLine requestLine;
  private final Map<String, String> headers = new HashMap<>();

  public HttpRequest(InputStream in) {
    BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

    try {
      String line = br.readLine();
      this.requestLine = new RequestLine(line);

      // 요청 헤더 n
      line = br.readLine();
      while (line != null && !"".equals(line)) {
        log.debug("header: {}", line);
        String[] tokens = line.split(":");
        this.headers.put(tokens[0].trim(), tokens[1].trim());
        line = br.readLine();
      }

      // 요청 body
      if (this.requestLine.getMethod().isPost()) {
        String body = IOUtils.readData(br, Integer.parseInt(this.headers.get("Content-Length")));
        log.debug(body);
        this.requestLine.getParameters().putAll(HttpRequestUtils.parseQueryString(body));
      }

    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private boolean isFormPost() {
    return "application/x-www-form-urlencoded".equals(this.headers.get("Content-Type"));
  }

  public HttpMethod getMethod() {
    return this.requestLine.getMethod();
  }

  public String getHeader(String key) {
    return this.headers.get(key);
  }

  public String getParameter(String key) {
    return this.requestLine.getParameters().get(key);
  }

  public String getPath() {
    return this.requestLine.getPath();
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

  public String getCookie() {
    return getHeader("Cookie");
  }

  public boolean isLogin() {
    Map<String, String> cookies = HttpRequestUtils.parseCookies(this.headers.get("Cookie"));
    return Boolean.parseBoolean(cookies.get("logined"));
  }
}
