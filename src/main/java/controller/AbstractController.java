package controller;

import model.HttpMethod;
import model.HttpRequest;
import model.HttpResponse;

public abstract class AbstractController implements Controller {
  @Override
  public void service(HttpRequest request, HttpResponse response) {
    HttpMethod method = request.getMethod();
    if (method.isGet()) {
      doGet(request, response);
      return;
    }

    if (method.isPost()) {
      doPost(request, response);
      return;
    }

    throw new RuntimeException("Wrong Method Exception");
  }

  protected abstract void doGet(HttpRequest request, HttpResponse response);
  protected abstract void doPost(HttpRequest request, HttpResponse response);
}
