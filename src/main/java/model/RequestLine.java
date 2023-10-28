package model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import util.HttpRequestUtils;

import java.util.Map;

@Slf4j
@Getter
public class RequestLine {
  private HttpMethod method;
  private String path;
  private Map<String, String> parameters;

  public RequestLine(String requestLine) {
    log.info("request line: {}", requestLine);
    if (requestLine == null) {
      throw new IllegalStateException("Wrong Request Line");
    }

    String[] tokens = requestLine.split(" ");
    if (tokens.length != 3) {
      throw new IllegalStateException("Wrong Request Line");
    }

    this.method = HttpMethod.valueOf(tokens[0]);

    if (this.method.isPost()) {
      this.path = tokens[1];
      return;
    }

    String[] uri = tokens[1].split("[?]");
    this.path = uri[0];

    if (uri.length == 2) {
      this.parameters = HttpRequestUtils.parseQueryString(uri[1]);
    }
  }
}
