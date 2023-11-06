package webserver;

import controller.*;
import model.HttpRequest;
import model.HttpResponse;

import java.util.Map;

public class RequestMapping {

  private static final Map<String, Controller> CONTROLLER_MAP;

  static {
    CONTROLLER_MAP = Map.of(
            "/user/list", UserListController.create(UserService.create()),
            "/user/login", UserLoginController.create(UserService.create()),
            "/user/create", UserCreateController.create(UserService.create())
            ,"NOT_FOUND", NotFoundController.create()
    );
  }

  public static Controller getController(String path) {
    return CONTROLLER_MAP.get(path) != null ? CONTROLLER_MAP.get(path) : CONTROLLER_MAP.get("NOT_FOUND");
  }
}
