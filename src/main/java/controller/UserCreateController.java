package controller;

import lombok.extern.slf4j.Slf4j;
import model.HttpRequest;
import model.HttpResponse;
import model.User;
import webserver.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
public class UserCreateController extends AbstractController {
  private static UserCreateController object;
  private final UserService userService;

  public static UserCreateController create(UserService userService) {

    if (object == null) {
      object = new UserCreateController(userService);
    }
    return object;
  }
  private UserCreateController(UserService userService) {
    this.userService = userService;
  }

  @Override
  protected void doPost(HttpRequest request, HttpResponse response) {
    getUserInfo(request).ifPresentOrElse(
            (user) -> {
              userService.saveUser(user);
              response.sendRedirect("/index.html");
            },
            () -> {
              throw new IllegalArgumentException("Wrong Query String Parameters.");
            }
    );
  }

  @Override
  protected void doGet(HttpRequest request, HttpResponse response) {
    return;
  }

  private Optional<User> getUserInfo(HttpRequest request) {
    for (String key : List.of("userId", "password", "name", "email")) {
      if (isStringEmpty(request.getParameter(key))) {
        return Optional.empty();
      }
    }

    User user = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"), request.getParameter("email"));
    log.info("setUserInfo : {}", user);

    return Optional.of(user);
  }

  private boolean isStringEmpty(String str) {
    return str == null || str.isEmpty();
  }
}
