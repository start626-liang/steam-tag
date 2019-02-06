package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *  加载json文件，在访问HTML文件，在解析录入数据库.
 */
public class AppParse {

  private HikariDataSource ds;
  private static final Logger log = Logger.getLogger("com.my.app");

  /**
   * 初始化连接池.
   * @param minimum 池中最小空闲链接数量
   * @param maximum 池中最大链接数量
   */
  public void init(int minimum,int maximum) {
    //连接池配置
    final HikariConfig config = new HikariConfig();
    config.setDriverClassName("org.mariadb.jdbc.MariaDbDataSource");
    config.setJdbcUrl(
        "jdbc:mysql://127.0.0.1:3306/testdb"
            + "?user=root&password=H171023&useUnicode=true&characterEncoding=utf8");
    config.addDataSourceProperty("cachePrepStmts", true);
    config.addDataSourceProperty("prepStmtCacheSize", 500);
    config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
    config.setConnectionTestQuery("SELECT 1");
    config.setAutoCommit(true);
    config.setMinimumIdle(minimum);
    config.setMaximumPoolSize(maximum);

    ds = new HikariDataSource(config);
  }


  /**
   * @return 返回一个数据库连接
   */
  private Connection getConnection() {
    try {
      return ds.getConnection();
    } catch (SQLException e) {
      log.severe(e.toString());
      return null;
    }
  }


  /**
   * 解析json文件.
   * @return json字符串
   */
  private static String loadJson() {
    Path file = null;
    BufferedReader bufferedReader = null;
    final String relativelyPath = System.getProperty("user.dir");
    final StringBuffer ss = new StringBuffer();
    String json = null;
    try {
      file = Paths.get(relativelyPath + "/src/main/resources/a.json");
      final InputStream inputStream = Files.newInputStream(file);

      bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
      String s = null;
      while ((s = bufferedReader.readLine()) != null) {
        ss.append(s);
      }
      json = ss.toString();
    } catch (IOException e) {
      log.severe(e.toString());
    }
    try {
      if (bufferedReader != null) {
        bufferedReader.close();
      }
    } catch (IOException ioe) {
      log.severe(ioe.toString());
    }
    return json;
  }

  /**
   * 插入数据.
   * @param games 游戏库列表对象
   */
  private static void db(Games[] games) {
    final AppParse ds = new AppParse();
    // 初始化数据库连接池.
    ds.init(10, 50);

    // 加载属性映射
    final Properties p1 = new Properties();
    FileInputStream in = null;
    try {
      in = new FileInputStream("user.properties");
    } catch (FileNotFoundException e) {
      log.severe(e.toString());
    }
    PreparedStatement ps = null;
    try {
      String appTable = null;
      try {
        p1.load(in);
        appTable = (String) p1.get("appTable");

      } catch (IOException e) {
        log.severe(e.toString());
      } finally {
        if (in != null) {
          in.close();
        }
      }
      final Connection conn = ds.getConnection();
      //解析HTML文件
      Document document = null;

      //关闭自动提交
//      boolean autoCommit = conn.getAutoCommit();
//      conn.setAutoCommit(false);
      ps = conn.prepareStatement("INSERT INTO "
          + appTable
          + "(id, name) VALUES (?,?)");
      final int appNum = games.length;
      for (int i = 0; i < appNum; ++i) {
        try {
          document = Jsoup.connect("https://store.steampowered.com/app/" + games[i].getAppid()).get();
        } catch (Exception e) {
          log.warning(e.toString());
        }
        String s = null;
        try {
          s = document.select("div.apphub_AppName").first().text();
        } catch (NullPointerException npe) {
          log.info(npe.toString() + "，id：" + i);
        } catch (Exception e) {
          log.severe(e.toString() + "，id：" + i);
        }
        ps.setInt(1, games[i].getAppid());
        ps.setString(2, s);
        ps.addBatch();
      }
      //      stmt.close();

      ps.executeBatch();
      conn.commit();
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

  public static void main(String[] args) {
    final ObjectMapper mapper = new ObjectMapper();
    final ArrayType arrayType = mapper.getTypeFactory().constructArrayType(Games.class);
    Games[] games = new Games[0];

    final String json = loadJson();
    try {
      games = mapper.readValue(json, arrayType);
    } catch (IOException e) {
      log.severe(e.toString());
    }
    db(games);
  }
}
