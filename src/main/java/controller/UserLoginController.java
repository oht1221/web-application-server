package controller;

import model.HttpRequest;
import model.HttpResponse;
import model.User;
import webserver.UserService;

import java.util.Collection;

public class UserLoginController extends AbstractController {
  private static UserLoginController object;
  private final UserService userService;

  public static UserLoginController create(UserService userService) {
    if (object == null) {
      object = new UserLoginController(userService);
    }
    return object;
  }
  private UserLoginController(UserService userService) {
    this.userService = userService;
  }

  @Override
  protected void doGet(HttpRequest request, HttpResponse response) {
    return;
  }

  @Override
  protected void doPost(HttpRequest request, HttpResponse response) {
    User user = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"), request.getParameter("email"));
    if (userService.login(user)) {
      response.addHeader("Set-Cookie", "logined=true");
      response.sendRedirect("/index.html");
      return;
    }
    response.sendRedirect("/user/login_failed.html");
  }
}
