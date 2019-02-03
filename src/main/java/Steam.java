import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import java.io.File;
import java.io.IOException;

/**
 * 验证文件是否存在
 */
public class Steam {
  public static void main(String[] args) {
    File file = new File("src/main/resources/games.json");
    System.out.println(file.isFile());
//    JsonFactory factory = new JsonFactory();
//// configure, if necessary:
//    factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
    JsonFactory jsonFactory = new JsonFactory(); // or, for data binding, org.codehaus.jackson.mapper.MappingJsonFactory
    try {
      JsonParser jp = jsonFactory.createParser(file); // or URL, Stream, Reader, String, byte[]
      System.out.println(jp);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
