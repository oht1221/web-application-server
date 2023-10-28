package util;

import model.RequestLine;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RequestLineTest {
  @Test
  void getMethod() {
    RequestLine requestLine = new RequestLine("GET /index.html?test1=1&test2=2 HTTP/1.1");
    assertTrue(requestLine.getMethod().isGet());
  }

  @Test
  void getPath() {
    RequestLine requestLine = new RequestLine("GET /index.html?test1=1&test2=2 HTTP/1.1");
    assertEquals("/index.html", requestLine.getPath());
  }

  @Test
  void getParameters() {
    RequestLine requestLine = new RequestLine("GET /index.html?test1=1&test2=2 HTTP/1.1");
    assertEquals(Map.of("test1", "1", "test2", "2"), requestLine.getParameters());
  }
}
