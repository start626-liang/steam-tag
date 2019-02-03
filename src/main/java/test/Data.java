package test;

import java.util.ArrayList;

public class Data {
  private ArrayList<Games> gamesList;

  public void assArrayList(Games games) {
    gamesList.add(games);
  }
  public ArrayList<Games> getGamesList() {
    return gamesList;
  }

  public String toString() {
    return "游戏数量:" + gamesList.size();
  }
}
