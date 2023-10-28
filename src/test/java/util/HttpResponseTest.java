package util;

import lombok.extern.slf4j.Slf4j;
import model.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

@Slf4j
public class HttpResponseTest {
  @Test
  void responseForwardTest() {
    HttpResponse response = new HttpResponse(createOutputStream());
    response.forward("/index.html");
    OutputStream outputStream = response.getOutputStream();
    log.info("output: {}", outputStream.toString());
  }

  @Test
  void responseRedirect() {
    HttpResponse response = new HttpResponse(createOutputStream());
    response.sendRedirect("/index.html");
    OutputStream outputStream = response.getOutputStream();

    log.info("output: \r\n{}", outputStream.toString());
  }

  @Test
  void responseCookies() {
    HttpResponse response = new HttpResponse(createOutputStream());
    response.addHeader("Set-Cookie", "logined=true");
    response.sendRedirect("/index.html");
    OutputStream outputStream = response.getOutputStream();

    log.info("output: \r\n{}", outputStream.toString());
  }

  private OutputStream createOutputStream() {
    return new ByteArrayOutputStream();
  }
}
