import java.util.logging.Logger;

/**
 * 记录日志
 */
public class LogTest {
  private static final Logger log = Logger.getLogger("com.my.app");
  public static void main(String[] args) {
    //全局日志
//    Logger.getGlobal().severe("3333333333333333333333");

    log.info("2222222222222");
    log.warning("ppppppppppppppppppp");
    log.severe("--------------");
  }
}
