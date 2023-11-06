package util;

import ch.qos.logback.core.net.SyslogOutputStream;
import lombok.extern.slf4j.Slf4j;
import model.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class HttpResponseTest {
  @Test
  void responseForwardTest() throws IOException {
    HttpResponse response = new HttpResponse(System.out);
    response.forward("/index.html");
    OutputStream outputStream = response.getOutputStream();
  }

  @Test
  void responseRedirect() {
    HttpResponse response = new HttpResponse(System.out);
    response.sendRedirect("/index.html");
  }

  @Test
  void responseCookies() {
    HttpResponse response = new HttpResponse(System.out);
    response.addHeader("Set-Cookie", "logined=true");
    response.sendRedirect("/index.html");
  }
}
