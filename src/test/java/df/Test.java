package df;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.stock.app.dao.StockDAO;
import com.stock.app.dao.StockDailyDAO;
import com.stock.app.entity.Stock;
import com.stock.app.entity.StockDaily;
import com.sun.javafx.binding.StringFormatter;

public class Test {

	static String stockdetail = "http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/%s.phtml?year=%s&jidu=%s";

	public static String getStockId(String htmlContent){
        String result=null;
        Pattern pattern = Pattern.compile("(?<=\\()(.+?)(?=\\))");
        Matcher matcher = pattern.matcher(htmlContent);
        while(matcher.find())
        	result=matcher.group();
        return result;
    }
	
	public static void main(String[] args) throws IOException {
		String url="http://quote.eastmoney.com/stocklist.html";
		Document doc = Jsoup.connect(url)
				.userAgent(
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
				.proxy("web-proxy.chn.hp.com", 8080).get();
		Element element = doc.getElementById("quotesearch");
		Elements sltits=element.select("div.sltit");
		Element shUL=null,szUL=null;
		List<String> shStockList=new ArrayList<String>();
		List<String> szStockList=new ArrayList<String>();
		
		for(int i=0;i<sltits.size();i++){
			Element tmp=sltits.get(i);
			if(tmp.html().indexOf("sh")>=0){
				shUL=tmp.nextElementSibling();
			}else if(tmp.html().indexOf("sz")>=0){
				szUL=tmp.nextElementSibling();
			}
		}
		if(shUL!=null){
			Elements liElements=shUL.select("li");
			for(int i=0;i<liElements.size();i++){
				String stockId=getStockId(liElements.get(i).html());
				if(stockId.startsWith("60")){
					String sub=liElements.get(i).html().substring(0, liElements.get(i).html().lastIndexOf(stockId));
					System.out.println(sub.substring(sub.lastIndexOf(">")+1,sub.lastIndexOf("(")));
					shStockList.add(stockId);
					break;
				}
			}
		}
		if(szUL!=null){
			Elements liElements=szUL.select("li");
			for(int i=0;i<liElements.size();i++){
				String stockId=getStockId(liElements.get(i).html());
				if(stockId.startsWith("00")||stockId.startsWith("30")){
					szStockList.add(stockId);
				}
			}
		}
		System.out.println("SH="+shStockList.size());
		System.out.println("SZ="+szStockList.size());
		
//		.get(0);
		//		loadDaily();
//		List<Float> highArr=new ArrayList<Float>();
//		highArr.add(1.1f);
//		highArr.add(1.2f);
//		highArr.add(1.3f);
//		highArr.add(1.5f);
//		highArr.add(1.3f);
//		highArr.add(1.1f);
//		highArr.add(1.2f);
//		highArr.add(1.3f);
//		highArr.add(1.1f);
//		
//		System.out.println(StringUtils.join(highArr.toArray(), ","));
	}
	public static void loadDaily() throws IOException {
		Calendar now=Calendar.getInstance();
		int year=now.get(Calendar.YEAR);
		int currentMonth = now.get(Calendar.MONTH) + 1; 
		int jidu=0;
        if (currentMonth >= 1 && currentMonth <= 3) 
        	jidu=1; 
        else if (currentMonth >= 4 && currentMonth <= 6) 
        	jidu=2; 
        else if (currentMonth >= 7 && currentMonth <= 9) 
        	jidu=3; 
        else if (currentMonth >= 10 && currentMonth <= 12) 
        	jidu=4; 
		ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext.xml");
		StockDAO stockDAO=(StockDAO) context.getBean("stockDAO");
		StockDailyDAO stockDailyDAO=(StockDailyDAO) context.getBean("stockDailyDAO");
		List list=stockDAO.findAll();
		int count=0;
		for (Object object : list) {
			Stock stock=(Stock) object;
			System.out.println("=======================start saving"+stock.getStockId()+"========================");
			for(int index=1;index<=jidu;index++){
				String url=StringFormatter.format(stockdetail, stock.getStockId(),year,index).getValue();
				Document doc = Jsoup.connect(url).timeout(0)
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
						.proxy("web-proxy.chn.hp.com", 8080).get();
				/*
				if(index==1){
					if(stock.getTotalMart()==0f){
//						hqDetails
						stock.setTotalMart(Float.parseFloat(doc.getElementById("totalMart2").text()));
						stock.setCurrentMart(Float.parseFloat(doc.getElementById("currMart2").text()));
						stockDAO.merge(stock);
					}
				}
				*/
				
				Element element = doc.getElementById("FundHoldSharesTable");
				if(element==null)
					continue;
				Elements tbodys = element.select("tbody");
				Element tbody = tbodys.get(0);
				Elements trs = tbody.select("tr");
				List<StockDaily> dailyList=new ArrayList<StockDaily>();
				if(trs.size()>0){
					for (int i = trs.size() - 1; i > 0; i--) {
						Element tr = trs.get(i);
						Elements tds = tr.select("td");
						StockDaily daily=new StockDaily();
						int date_col=1,open_col=2,max_col=3,close_col=4,min_col=5,vol_col=6,mon_col=7;
						Element td = tds.get(date_col-1);
						Element contentE = td.select("div[align='center']>a").get(0);
						try {
							daily.setTradeDate(convertDate(contentE.text()).getTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						td = tds.get(open_col-1);
						contentE = td.select("div[align='center']").get(0);
						String text=contentE.text();
						daily.setOpenValue(getValue(text));
						
						td = tds.get(max_col-1);
						contentE = td.select("div[align='center']").get(0);
						text=contentE.text();
						daily.setMaxValue(getValue(text));
						
						td = tds.get(close_col-1);
						contentE = td.select("div[align='center']").get(0);
						text=contentE.text();
						daily.setCloseValue(getValue(text));
						
						
						td = tds.get(min_col-1);
						contentE = td.select("div[align='center']").get(0);
						text=contentE.text();
						daily.setMinValue(getValue(text));
						
						td = tds.get(vol_col-1);
						contentE = td.select("div[align='center']").get(0);
						text=contentE.text();
						if(text!=null&&text.length()>0)
							daily.setTradeVol(isNumeric(text)?Long.parseLong(text):0l);
						else
							daily.setTradeVol(0l);
						
						td = tds.get(mon_col-1);
						contentE = td.select("div[align='center']").get(0);
						text=contentE.text();
						if(text!=null&&text.length()>0)
							daily.setTradeSum(isNumeric(text)?Long.parseLong(text):0l);
						else
							daily.setTradeSum(0l);
						
						
						daily.setStockId(stock.getStockId());
						dailyList.add(daily);
						
					}
					stockDailyDAO.save(dailyList);
				}
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
			}
			System.out.println("=======================end saving"+stock.getStockId()+"========================");
			break;
		}
//		OptDAO optDAO=(OptDAO) context.getBean("optDAO");
		
		
	}

	public static float getValue(String str){
		float result=0f;
		try {
			if(str!=null&&str.length()>0)
				result=Float.parseFloat(str);
		} catch (NumberFormatException e) {
		} finally{
			return result;
		}
	}
	public static boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}
	
	private static Date convertDate(String dateStr) throws ParseException{
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.parse(dateStr);
	}
	
	public static void getDailyDemo() throws IOException {
		Document doc = Jsoup.connect(stockdetail)
				.userAgent(
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
				.proxy("web-proxy.chn.hp.com", 8080).get();
		//totalMart2
		//currMart2
		
		
		Element element = doc.getElementById("FundHoldSharesTable");
		Elements tbodys = element.select("tbody");
		Element tbody = tbodys.get(0);
		Elements trs = tbody.select("tr");
		for (int i = trs.size() - 1; i > 0; i--) {
			Element tr = trs.get(i);
			Elements tds = tr.select("td");
			for (int j = 0; j < tds.size(); j++) {
				Element td = tds.get(j);
				Element contentE = null;
				if (j == 0)
					contentE = td.select("div[align='center']>a").get(0);
				else
					contentE = td.select("div[align='center']").get(0);
				System.out.print(contentE.text() + "\t");
			}
			System.out.println();
		}
	}

	public static void getAllStocks() throws IOException {
		Document doc = Jsoup.connect("http://bbs.10jqka.com.cn/codelist.html").userAgent(
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
				// .proxy("web-proxy.chn.hp.com",8080)
				.get();
		Element element = doc.getElementById("sh");
		Element ulElement = null;
		for (Node node : element.siblingNodes()) {
			if (node.nodeName().equals("ul")) {
				ulElement = (Element) node;
				break;
			}
		}
		if (ulElement != null) {
			Elements lis = ulElement.select("li");
			for (int i = 0; i < lis.size(); i++) {
				Element li = lis.get(i);
				Element a = li.select("a").get(0);
				String[] arr = a.text().split(" ");
				System.out.println(arr[0] + "=" + arr[1]);
			}
		}
	}
}
