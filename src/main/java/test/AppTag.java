package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 游戏和游戏标签绑定
 */
public class AppTag {
  private static final Logger log = Logger.getLogger("com.my.tag");
  public static void main(String[] args) {
    final Json json  = new Json();
    final Games[] games = json.appObjectArray();
    final DB db = new DB();
    // 初始化数据库连接池.
    db.init(10, 50);

    // 加载属性映射
    final Properties p = new Properties();
    FileInputStream in = null;
    try {
      in = new FileInputStream("user.properties");
    } catch (FileNotFoundException e) {
      log.severe(e.toString());
    }
    PreparedStatement appTagPs = null;
    PreparedStatement tagPs = null;
    try {
      String appTagTable = null;
      String tagTable = null;
      try {
        p.load(in);
        appTagTable = (String) p.get("appTagTable");
        tagTable = (String) p.get("tagTable");
      } catch (IOException e) {
        log.severe(e.toString());
      } finally {
        if (in != null) {
          in.close();
        }
      }
      final Connection conn = db.getConnection();
      //解析HTML文件
      Document document = null;

      //关闭自动提交
//      boolean autoCommit = conn.getAutoCommit();
//      conn.setAutoCommit(false);
      appTagPs = conn.prepareStatement("INSERT INTO "
          + appTagTable
          + "(tag, app, ver) VALUES (?, ?, ?)");
      tagPs = conn.prepareStatement("SELECT id FROM "
          + tagTable
          + " WHERE name=?");
      final int appNum = games.length;
      ResultSet rs = null;
      for (int i = 0; i < appNum; ++i) {
        final int appid = games[i].getAppid();
        try {
          document = Jsoup.connect("https://store.steampowered.com/app/" + appid).get();
        } catch (Exception e) {
          log.warning(e.toString());
        }
        try {
          Elements eList = document.select("a.app_tag");
          for (Element e : eList) {
            tagPs.setString(1, e.text());
            rs = tagPs.executeQuery();
            rs.next();

            try {
              if(rs.isLast()){
                final int id  = rs.getInt("id");
                final String varStr = String.valueOf(id) + String.valueOf(appid);
                appTagPs.setInt(1, id);
                appTagPs.setInt(2, appid);
                appTagPs.setString(3, varStr);
                appTagPs.addBatch();
              } else {
                log.severe("返回结果超过一个");
              }
            } catch (Exception ex) {
              log.severe(ex.toString());
            }
          }
        } catch (NullPointerException npe) {
          log.info(npe.toString());
        } catch (Exception ex) {
          log.severe(ex.toString());
        }

        try {
          appTagPs.executeBatch();
          conn.commit();
        } catch (BatchUpdateException bue) {
          log.warning(bue.toString());
        }
      }
      //      stmt.close();
//      conn.setAutoCommit(autoCommit);

      conn.close();
    } catch (SQLException e) {
      log.severe(e.toString());
    } catch (IOException e) {
      log.severe(e.toString());
    } finally {
      try {
        if (tagPs != null) {
          tagPs.close();
        }
      } catch (SQLException e) {
        log.severe(e.toString());
      }
      try {
        if (appTagPs != null) {
          appTagPs.close();
        }
      } catch (SQLException e) {
        log.severe(e.toString());
      }
    }
  }
}
