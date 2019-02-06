package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TagTest {
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
    PreparedStatement ps = null;
    try {
      String table = null;
      try {
        p.load(in);
        table = (String) p.get("tagTable");
        log.info(table);
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
      ps = conn.prepareStatement("INSERT INTO "
          + table
          + "(name) VALUES (?)");
      final int appNum = games.length;
      for (int i = 0; i < appNum; ++i) {

        try {
          document = Jsoup.connect("https://store.steampowered.com/app/" + games[i].getAppid()).get();
        } catch (Exception e) {
          log.warning(e.toString());
        }
        try {
          Elements eList = document.select("a.app_tag");
          for (Element e : eList) {
            ps.setString(1, e.text());
            ps.addBatch();
          }
        } catch (NullPointerException npe) {
          log.info(npe.toString());
        } catch (Exception e) {
          log.severe(e.toString());
        }

        try {
          ps.executeBatch();
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
        if (ps != null) {
          ps.close();
        }
      } catch (SQLException e) {
        log.severe(e.toString());
      }
    }
  }
}
