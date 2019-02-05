package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Json {
  private static final Logger log = Logger.getLogger("com.my.load");
  /**
   * 解析json文件成字符串.
   * @return json字符串
   */
  public String JsonStr() {
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

  public Games[] appObjectArray() {
    final ObjectMapper mapper = new ObjectMapper();
    final ArrayType arrayType = mapper.getTypeFactory().constructArrayType(Games.class);
    Games[] games = new Games[0];

    final String json = JsonStr();
    try {
      games = mapper.readValue(json, arrayType);
    } catch (IOException e) {
      log.severe(e.toString());
    }
    return games;
  }
}
