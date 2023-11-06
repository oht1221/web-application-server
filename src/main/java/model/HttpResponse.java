package model;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Setter
public class HttpResponse {
  private OutputStream os;
  private Status status;
  private Map<String, String> headers = new HashMap<>();

  public HttpResponse(OutputStream os) {
    this.os = new DataOutputStream(os);
  }

  public OutputStream getOutputStream() {
    return this.os;
  }

  public void addHeader(String key, String value) {
    this.headers.put(key, value);
  }

  public void forward(String path) {
    try {
      byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
      String[] pathTokens = path.split("[.]");
      String resourceType = path.split("[.]")[pathTokens.length - 1];

      switch (resourceType) {
        case "css" -> headers.put("Content-Type", "text/css");
        case "javascript" -> headers.put("Content-Type", "application/javascript");
        case "html" -> headers.put("Content-Type", "text/html;charset=utf-8");
      }

      response200Header();
      responseBody(body);
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  public void forwardBody(String body) {
    this.headers.put("Content-Type", "text/html;charset=utf-8");
    this.headers.put("Content-Length", String.valueOf(body.length()));
    response200Header();
    responseBody(body.getBytes());
  }

  public void sendRedirect(String location) {
    try {
      this.os.write("HTTP/1.1 302 FOUND \r\n".getBytes());
      processHeader();
      this.os.write(("Location: " + location + " \r\n").getBytes());
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void response200Header() {
    try {
      this.os.write("HTTP/1.1 200 OK \r\n".getBytes());
      processHeader();
      //body 전 공백 한 줄
      os.write("\r\n".getBytes());

    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void responseBody(byte[] body) {
    try {
      os.write(body);
      os.write("\r\n".getBytes());
      os.flush();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void writeStatus() {
    StringBuilder sb = new StringBuilder("HTTP/1.1 ").append(this.status.getValue()).append(" ").append(this.status).append(" \r\n");
    try {
      os.write(sb.toString().getBytes());
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void processHeader() {
    StringBuilder sb = new StringBuilder();
    try {
      for (String key : this.headers.keySet()) {
        sb.append(key).append(": ").append(this.headers.get(key)).append(" \r\n");
        this.os.write(sb.toString().getBytes());
        sb.delete(0, sb.length());
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void writeHeaders() {
    try {
      for (String key: this.headers.keySet()) {
        os.write((key + ": " + this.headers.get(key) + "\r\n").getBytes());
      }
      //body 전 공백 한 줄
      os.write("\r\n".getBytes());
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
