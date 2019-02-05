import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * 属性映射
 */
public class PropertyTest {
  private static final Logger log = Logger.getLogger("com.my.p");
  public static void main(String[] args) {
    // 创建属性映射
    Properties p = new Properties();
    p.put("appTable" , "app_test");
    try {
      FileOutputStream out = new FileOutputStream("user.properties");
      p.store(out, "user properties");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // 加载属性映射
    Properties p1 = new Properties();
    try {
      FileInputStream in = new FileInputStream("user.properties");
      try {
        Logger.getGlobal().info((String) p1.get("appTable"));
        p1.load(in);
        Logger.getGlobal().info((String) p1.get("appTable"));

      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }
}
