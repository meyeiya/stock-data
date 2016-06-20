package df;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class Test {

	static String stockdetail="http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/601006.phtml?year=2016&jidu=1"; 
	
	public static void main(String[] args) throws IOException {
		Document doc = Jsoup.connect(stockdetail)
				  .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
//				  .proxy("web-proxy.chn.hp.com",8080)
				  .get();
		Element element=doc.getElementById("FundHoldSharesTable");
		Elements tbodys=element.select("tbody");
		Element tbody=tbodys.get(0);
		Elements trs=tbody.select("tr");
		for(int i=trs.size()-1;i>0;i--){
			Element tr=trs.get(i);
			Elements tds=tr.select("td");
			for(int j=0;j<tds.size();j++){
				Element td=tds.get(j);
				Element contentE=null;
				if(j==0)
					contentE=td.select("div[align='center']>a").get(0);
				else
					contentE=td.select("div[align='center']").get(0);
				System.out.print(contentE.text()+"\t");
			}
			System.out.println();
		}
	}
	
	public static void getAllStocks() throws IOException{
		Document doc = Jsoup.connect("http://bbs.10jqka.com.cn/codelist.html")
				  .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
//				  .proxy("web-proxy.chn.hp.com",8080)
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
					Elements lis=ulElement.select("li");
					for(int i=0;i<lis.size();i++){
						Element li=lis.get(i);
						Element a=li.select("a").get(0);
						String[] arr=a.text().split(" ");
						System.out.println(arr[0]+"="+arr[1]);
					}
				}
	}
}
