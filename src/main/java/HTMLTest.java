
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import org.jsoup.select.Elements;
import java.io.File;


/**
 * 解析html文件
 */
public class HTMLTest {
  public static void main(String[] args) throws IOException {
    String favImage = "Not Found";
    try
    {
      File f = new File("te2st.html");
      System.out.println(f.getAbsolutePath());
      System.out.println(f.isFile());
      Document document = Jsoup.connect("http://www.yiibai.com").get();
      System.out.println(document.title());

      Document document1 = Jsoup.parse( new File( "test.html" ) , "utf-8" );
      System.out.println("parse---------" + document1.title());

      String html = "<html><head><title>First parse</title></head>"
          + "<body><p>Parsed HTML into a doc.</p></body></html>";
      Document document2 = Jsoup.parse(html);
      System.out.println(document2.title());


//      Document document3 = Jsoup.parse(new File("test.html"), "utf-8");
      Document document3 = Jsoup.connect("http://www.yiibai.com").get();
      Element element = document3.head().select("link[href~=.*\\.(ico|png)]").first();
      if (element == null)
      {
        element = document3.head().select("meta[itemprop=image]").first();
        if (element != null)
        {
          favImage = element.attr("content");
        }
      }
      else
      {
        favImage = element.attr("href");
      }

      Document document4 = Jsoup.connect("https://store.steampowered.com/app/552990").get();
      Elements links = document4.select("a[href]");
      for (Element link : links)
      {
        System.out.println("link : " + link.attr("href"));
        System.out.println("text : " + link.text());
      }

      Document document5 = Jsoup.connect("http://www.yiibai.com").get();

      String description = document5.select("meta[name=description]").get(0).attr("content");
      System.out.println("Meta description : " + description);

      String keywords = document5.select("meta[name=keywords]").first().attr("content");
      System.out.println("Meta keyword : " + keywords);


//      Document doc = Jsoup.parse(new File("c:/temp/yiibai-index.html"),"utf-8");
//      Element formElement = doc.getElementById("loginForm");
//
//      Elements inputElements = formElement.getElementsByTag("input");
//      for (Element inputElement : inputElements) {
//        String key = inputElement.attr("name");
//        String value = inputElement.attr("value");
//        System.out.println("Param name: "+key+" \nParam value: "+value);
//      }

//      Document document6 = Jsoup.parse(new File("C:/Users/zkpkhua/Desktop/yiibai.com.html"), "utf-8");
//      Elements links2 = document6.select("a[href]");
//      links2.attr("rel", "nofollow");

    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    System.out.println("favImage-------------" + favImage);
  }
}
