package model;

public enum HttpMethod {
  GET,
  POST;

  public boolean isPost() {
    return this.equals(POST);
  }

  public boolean isGet() {
    return this.equals(GET);
  }
}
