package util;

import model.HttpRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.io.*;
import java.nio.charset.StandardCharsets;

public class HttpRequestTest {
  private String testDirectory = "./src/test/resources/";

  @Test
  public void request_GET() throws FileNotFoundException {
    InputStream is = new FileInputStream(new File(testDirectory + "Http_GET.txt"));

    HttpRequest httpRequest = new HttpRequest(is);

    assertEquals("oht1221", httpRequest.getParameter("userId"));
    assertTrue(httpRequest.getMethod().isGet());
    assertEquals("keep-alive", httpRequest.getHeader("Connection"));
    assertEquals("/user/create", httpRequest.getPath());
  }

  @Test
  public void request_POST() throws FileNotFoundException {
    InputStream is = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

    HttpRequest httpRequest = new HttpRequest(is);

    assertEquals("oht1221", httpRequest.getParameter("userId"));
    assertTrue(httpRequest.getMethod().isPost());
    assertEquals("keep-alive", httpRequest.getHeader("Connection"));
    assertEquals("/user/create", httpRequest.getPath());
  }
}
