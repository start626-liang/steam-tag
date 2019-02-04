package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *  加载json文件，在访问HTML文件，在解析录入数据库.
 */
public class Parse {

  private HikariDataSource ds;
  private static final Logger log = Logger.getLogger("com.my.app");

  /**
   * 初始化连接池.
   * @param minimum 池中最小空闲链接数量
   * @param maximum 池中最大链接数量
   */
  public void init(int minimum,int maximum) {
    //连接池配置
    HikariConfig config = new HikariConfig();
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
    String relativelyPath = System.getProperty("user.dir");
    StringBuffer ss = new StringBuffer();
    String json = null;
    try {
      file = Paths.get(relativelyPath + "/src/main/resources/a.json");
      InputStream inputStream = Files.newInputStream(file);

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
   * 初始化数据库连接池.
   * @param games 游戏库列表
   */
  private static void db(Games[] games) {

    Parse ds = new Parse();
    ds.init(10, 50);

    try {
      Connection conn = ds.getConnection();
      //解析HTML文件
      Document document = null;

      for (int i = 0; i < games.length; ++i) {
        document = Jsoup.connect("https://store.steampowered.com/app/" + games[i].getAppid()).get();
        String s = null;
        try {
          s = document.select("div.apphub_AppName").first().text();
        } catch (NullPointerException e) {
          log.severe(e.toString() + "，id：" + i);
        }
        PreparedStatement ps = conn.prepareStatement("INSERT INTO appa (appid, name) VALUES (?,?)");

        ps.setInt(1, games[i].getAppid());
        ps.setString(2, s);


        try {
          ps.executeUpdate();
          ps.close();
        } catch (SQLIntegrityConstraintViolationException e) {
          log.severe(e.toString());
        }
      }
      //      stmt.close();

      conn.close();
    } catch (SQLException e) {
      log.severe(e.toString());
    } catch (IOException e) {
      log.severe(e.toString());
    }
  }

  public static void main(String[] args) {
    ObjectMapper mapper = new ObjectMapper();
    ArrayType arrayType = mapper.getTypeFactory().constructArrayType(Games.class);
    Games[] games = new Games[0];

    String json = loadJson();
    try {
      games = mapper.readValue(json, arrayType);
    } catch (IOException e) {
      log.severe(e.toString());
    }
    db(games);
  }
}
