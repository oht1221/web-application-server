package controller;

import model.HttpRequest;
import model.HttpResponse;
import model.User;
import util.HttpRequestUtils;
import webserver.UserService;

import java.util.Collection;
import java.util.Map;

public class UserListController extends AbstractController {
  private static UserListController object;
  private final UserService userService;

  public static UserListController create(UserService userService) {
    if (object == null) {
      object = new UserListController(userService);
    }
    return object;
  }
  private UserListController(UserService userService) {
    this.userService = userService;
  }

  @Override
  protected void doGet(HttpRequest request, HttpResponse response) {
    if (request.isLogin()) {
      Collection<User> Users = userService.getAllUsers();
      StringBuilder sbBody = new StringBuilder();
      sbBody.append("<table border='1'>");
      Users.forEach(user -> {
        sbBody.append("<tr>")
                .append("<td>").append(user.getUserId()).append("</td>")
                .append("<td>").append(user.getEmail()).append("</td>")
                .append("<td>").append(user.getName()).append("</td>")
                .append("</tr>");
      });
      sbBody.append("</table>");

      response.forwardBody(sbBody.toString());
      return;
    }
    response.sendRedirect("/user/login.html");
  }

  @Override
  protected void doPost(HttpRequest request, HttpResponse response) {
    return;
  }
}
