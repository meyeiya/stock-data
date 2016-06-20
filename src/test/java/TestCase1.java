import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.stock.app.dao.OptDAO;
import com.stock.app.dao.StockDAO;
import com.stock.app.entity.Opt;
import com.stock.app.entity.Stock;


public class TestCase1{

	public void testSave() throws IOException{
//		System.out.println(stockDAO);
		ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext.xml");
		StockDAO stockDAO=(StockDAO) context.getBean("stockDAO");
		OptDAO optDAO=(OptDAO) context.getBean("optDAO");
		
		Document doc = Jsoup.connect("http://bbs.10jqka.com.cn/codelist.html")
		  .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
		  .proxy("web-proxy.chn.hp.com",8080)
		  .get();
		Element element=doc.getElementById("sh");
		Element ulElement=null;
		for (Node node : element.siblingNodes()) {
			if(node.nodeName().equals("ul")){
				ulElement=(Element) node;
				break;
			}
		}
		if(ulElement!=null){
			List<Stock> stocks=new ArrayList<Stock>();
			Elements lis=ulElement.select("li");
			Opt opt=new Opt();
			opt.setOptTime(new Date().getTime());
			optDAO.save(opt);
			for(int i=0;i<lis.size();i++){
				Element li=lis.get(i);
				Element a=li.select("a").get(0);
				String[] arr=a.text().split(" ");
				Stock stock=new Stock();
				stock.setStockId(arr[1]);
				stock.setStockName(arr[0]);
				stock.setOpt(opt);
				stocks.add(stock);
			}
			stockDAO.save(stocks);
		}
	}
}
