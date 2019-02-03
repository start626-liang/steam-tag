package test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
class User implements Serializable {
  private static final long serialVersionUID = -5952920972581467417L;
  private int appid;

  public User() {
  }

  public User(int appid) {
    this.appid = appid;
  }

  public int getAppid() {
    return appid;
  }

  @Override
  public String toString() {
    return "User{" +
        "appid=" + appid +
        '}';
  }
}
