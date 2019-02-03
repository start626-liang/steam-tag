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

/**
 * 读取json文件并解析
 */
public class D {
//  public static void main(String args[]){
//    JacksonTester tester = new JacksonTester();
//
//      ObjectMapper mapper = new ObjectMapper();
//
//      File file = new File("src/main/resources/games.json");
//      System.out.println(file.isFile());
//      StringBuilder contentBuilder = new StringBuilder();
//
//      try (Stream<String> stream = Files.lines(
//          Paths.get("src/main/resources/games.json"), StandardCharsets.UTF_8))
//      {
//        stream.forEach(s -> contentBuilder.append(s).append("\n"));
//      }
//      catch (IOException e)
//      {
//        e.printStackTrace();
//      }
//
////      String expected = "[{\"name\":\"Ryan\"},{\"name\":\"Test\"},{\"name\":\"Leslie\"}]";
////      ArrayType arrayType = mapper.getTypeFactory().constructArrayType(User.class);
////      User[] users = mapper.readValue(expected, arrayType);
////      System.out.println(users.length);
//
//  }
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

    //解析json文件
    String as = "[{\"name\":     2222},{\"name\": 212222},{\"name\": 333}]";
    String i = "[{\"add\":     2222},{\"appid\": 212222},{\"appid\": 333}]";
    String i1 = "[{\"id\":     2222},{\"id\": 212222},{\"id\": 333}]";
//    System.out.println(as);
//    System.out.println(i);
    ObjectMapper mapper = new ObjectMapper();
    ArrayType arrayType = mapper.getTypeFactory().constructArrayType(Games.class);
    Games[] users = new Games[0];
//    System.out.println(json);

    try {
      users = mapper.readValue(json, arrayType);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(users[1]);
    System.out.println(users[1].getAppid());
  }
}
