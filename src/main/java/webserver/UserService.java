package webserver;

import db.DataBase;
import lombok.extern.slf4j.Slf4j;
import model.User;

import java.util.Collection;

@Slf4j
public class UserService {

  public User saveUser(User user) {
    if (DataBase.findUserById(user.getUserId()) != null) {
      throw new IllegalArgumentException("User ID already exists.");
    }

    DataBase.addUser(user);
    return user;
  }

  public boolean login(User user) {
    User foundUser = null;
    if ((foundUser = DataBase.findUserById(user.getUserId())) == null) {
      return false;
    }

    return foundUser.getPassword().equals(user.getPassword());
  }

  public Collection<User> getAllUsers() {
    return DataBase.findAll();
  }
}
