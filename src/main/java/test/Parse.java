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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Parse {

  private HikariDataSource ds;

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

  private Connection getConnection() {
    try {
      return ds.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void main(String[] args) {
    //读取jaon文件
    Path file = null;
    BufferedReader bufferedReader = null;
    String relativelyPath = System.getProperty("user.dir");
    StringBuffer ss = new StringBuffer();
    String json = null;
    try {
      file = Paths.get(relativelyPath + "/src/main/resources/a.json");
      InputStream inputStream = Files.newInputStream(file);

      bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String s = null;
      while ((s = bufferedReader.readLine()) != null){
        ss.append(s);
      }
      json = ss.toString();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        bufferedReader.close();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }

    ObjectMapper mapper = new ObjectMapper();
    ArrayType arrayType = mapper.getTypeFactory().constructArrayType(Games.class);
    Games[] games = new Games[0];

    try {
      games = mapper.readValue(json, arrayType);
    } catch (IOException e) {
      e.printStackTrace();
    }

    Parse ds = new Parse();
    ds.init(10, 50);

    //......
    //最后关闭链接
    try {
      Connection conn = ds.getConnection();
//      Statement stmt = conn.createStatement();
//      ResultSet rs = stmt.executeQuery("SELECT * FROM admin");
//      System.out.println(rs);
//
//      //STEP 5: Extract data from result set
//      while (rs.next()) {
//        //Retrieve by column name
//        int id  = rs.getInt("id");
//        String  user  = rs.getString("user");
//
//        //Display values
//        System.out.println("ID: " + id);
//        System.out.println(user);
//      }
//      rs.close();


//    System.out.println(document.select(".app_tag"));
//    System.out.println("tag:---------------");
//    for(Element e : document.select("a.app_tag")){
//      System.out.println(e.text());
//    }
//    System.out.println("-------------------");
      //解析HTML文件
      Document document = null;

      for(int i = 0; i < games.length; ++i) {
        document = Jsoup.connect("https://store.steampowered.com/app/" + games[i].getAppid()).get();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO app (appid, name) VALUES (?,?)");

        ps.setInt(1, games[i].getAppid());
        ps.setString(2, document.select("div.apphub_AppName").first().text());

        ps.executeUpdate();
      }
//      stmt.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
