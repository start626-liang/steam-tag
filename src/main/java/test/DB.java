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

}
