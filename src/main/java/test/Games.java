package test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Games implements Serializable {

  private int appid;

  public Games() {}
  public Games(int appid) {
    this.appid = appid;
  }


  @Override
  public String toString() {
    return "游戏ID:" + appid;
  }

  public int getAppid() {
    return appid;
  }

  public void setAppid(int appid) {
    this.appid = appid;
  }
}
