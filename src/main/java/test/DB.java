package test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DB {
  private HikariDataSource ds;
  private static final Logger log = Logger.getLogger("com.my.db");

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
  public Connection getConnection() {
    try {
      return ds.getConnection();
    } catch (SQLException e) {
      log.severe(e.toString());
      return null;
    }
  }

//  public static void main(String[] args) {
//    final DB db = new DB();
//    // 初始化数据库连接池.
//    db.init(10, 50);
//    final Connection conn = db.getConnection();
//    try {
//      PreparedStatement appTagPs = conn.prepareStatement("INSERT INTO "
//          + "app_tag_test"
//          + "(tag, app, ver) VALUES (?, ?, ?)");
//      PreparedStatement ps = conn.prepareStatement("SELECT id FROM tag_test WHERE name=?");
//      ps.setString(1, "FPS");
//      ResultSet rs = ps.executeQuery();
//      //STEP 5: Extract data from result set
//      while (rs.next()) {
//        //Retrieve by column name
//        if(rs.isLast()){
//          int id  = rs.getInt("id");
//          //Display values
//          System.out.println("ID: " + id);
//        }
//      }
//      appTagPs.setInt(1, 1);
//      appTagPs.setInt(2, 10);
//      appTagPs.setString(3, String.valueOf(1) + String.valueOf(10));
//      appTagPs.executeUpdate();
//      rs.close();
//      ps.close();
//      conn.close();
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//  }
}
