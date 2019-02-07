package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 补全未游戏库的游戏名称.
 */
public class Complemented {
  private static final Logger log = Logger.getLogger("com.my.complemented");

  public static void main(String[] args) {
    // 加载属性映射
    final Properties p = new Properties();
    FileInputStream in = null;
    try {
      in = new FileInputStream("user.properties");
    } catch (FileNotFoundException e) {
      log.severe(e.toString());
    }
    String table = null;
    try {
      p.load(in);
      table = (String) p.get("appTable");
    } catch (IOException e) {
      log.severe(e.toString());
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          log.severe(e.toString());
        }
      }
    }


    final DB db = new DB();
    // 初始化数据库连接池.
    db.init(10, 50);
    final Connection conn = db.getConnection();
    try {
      PreparedStatement selectPs = conn.prepareStatement("select id from "
          + table
          + " where name is null");
      PreparedStatement updatePs = conn.prepareStatement("UPDATE "
          + table
          + "SET name=? WHERE id=?");
      ResultSet rs = selectPs.executeQuery();
      Document document = null;
      while (rs.next()) {
        final int id = rs.getInt("id");
        try {
          document = Jsoup.connect("https://store.steampowered.com/app/" + id).get();
        } catch (Exception e) {
          log.warning(e.toString());
        }

        String s = null;
        try {
          s = document.select("div.apphub_AppName").first().text();
        } catch (NullPointerException npe) {
          log.info(npe.toString() + "，id：" + id);
        } catch (Exception e) {
          log.severe(e.toString() + "，id：" + id);
        }
        updatePs.setString(1, s);
        updatePs.setInt(2, id);
        updatePs.addBatch();
      }
      rs.close();
      updatePs.executeBatch();
      conn.commit();
      updatePs.close();
      selectPs.close();
      conn.close();
    } catch (SQLException e) {
      log.severe(e.toString());
    }

  }
}
