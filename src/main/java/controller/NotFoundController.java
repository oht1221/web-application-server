package controller;

import model.HttpRequest;
import model.HttpResponse;

public class NotFoundController extends AbstractController {
  private static NotFoundController notFoundController;

  public static NotFoundController create() {
    if (notFoundController == null) {
      notFoundController = new NotFoundController();
    }
    return notFoundController;
  }

  private NotFoundController() {

  }

  @Override
  protected void doGet(HttpRequest request, HttpResponse response) {
    return;
  }

  @Override
  protected void doPost(HttpRequest request, HttpResponse response) {
    return;
  }
}
