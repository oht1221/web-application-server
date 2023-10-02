package model;

public enum Status {
  OK("200"),
  REDIRECT("302"),
  NOT_FOUND("404");

  private String value;

  Status(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
