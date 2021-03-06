import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.stock.app.dao.OptDAO;
import com.stock.app.dao.StockDAO;
import com.stock.app.dao.StockDailyDAO;
import com.stock.app.entity.Opt;
import com.stock.app.entity.Stock;
import com.stock.app.entity.StockDaily;


public class TestCase1 extends BaseCase{

	List<StockDaily> lowList=new ArrayList<StockDaily>();
	List<StockDaily> highList=new ArrayList<StockDaily>();
	@Test
	public void testSave() throws IOException{
//		System.out.println(stockDAO);
		ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext.xml");
		StockDailyDAO stockDailyDAO=(StockDailyDAO) context.getBean("stockDailyDAO");
		List<StockDaily> stockDailiesList=stockDailyDAO.findByStockId("601006");
		getStock(stockDailiesList);
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		for (StockDaily stockDaily : lowList) {
			Calendar c=Calendar.getInstance();
			c.setTimeInMillis(stockDaily.getTradeDate());
			System.out.println(dateFormat.format(c.getTime())+"\t| L"+(lowList.indexOf(stockDaily)+1)+"\t| "+stockDaily.getMinValue());
		}
		for (StockDaily stockDaily : highList) {
			Calendar c=Calendar.getInstance();
			c.setTimeInMillis(stockDaily.getTradeDate());
			System.out.println(dateFormat.format(c.getTime())+"\t| H"+(highList.indexOf(stockDaily)+1)+"\t| "+stockDaily.getMaxValue());
		}
	}
	
	
	public void getStock(List<StockDaily> stockDailiesList){
		List<StockDaily> stockDailies=stockDailiesList;
		if(stockDailies.size()>0){
			int index=0;
			StockDaily tmp=null;
			StockDaily Lx=null,Hx=null;
			while(index+1<stockDailies.size()){
				for(int i=index;i<stockDailies.size();i++){
					tmp=stockDailies.get(i);
					if(tmp.getMinValue()>0f){
						Lx=tmp;
						index=i;
						break;
					}
				}
				if(index+1>=stockDailies.size()){
					return;
				}
				ArrayList<StockDaily> tmpList=new ArrayList<StockDaily>(stockDailies.subList(index+1, stockDailies.size()));
				StockDaily lowIn3=getLowIn3InList(Lx,(List)tmpList.clone());
				lowList.add(lowIn3);
				
				index=stockDailies.indexOf(lowIn3);
				if(index+1>=stockDailies.size()){
					return;
				}
				for(int i=index+1;i<stockDailies.size();i++){
					tmp=stockDailies.get(i);
					if(tmp.getMaxValue()>0f){
						Hx=tmp;
						index=i;
						break;
					}
				}
				if(index+1>=stockDailies.size()){
					return;
				}
				tmpList=new ArrayList<StockDaily>(stockDailies.subList(index+1, stockDailies.size()));
				StockDaily highIn3=getHighIn3InList(Hx,(List)tmpList.clone());
				highList.add(highIn3);
				
				index=stockDailies.indexOf(highIn3);
				if(index+1>=stockDailies.size()){
					return;
				}
				index++;
			}

		}
		
    }
	
	private StockDaily getLowIn3InList(StockDaily L0tmp,List<StockDaily> stockList){
		StockDaily returnDaily=L0tmp;
		for(int i=0;i<stockList.size();i=i+3){
			if(i>=stockList.size()){
				break;
			}
			int toIndex;
			if(i+3>=stockList.size()){
				toIndex=stockList.size()-1;
			}else{
				toIndex=i+3;
			}
			List<StockDaily> subList=stockList.subList(i, toIndex);
			Collections.sort(subList, new Comparator<StockDaily>() {

				@Override
				public int compare(StockDaily o1, StockDaily o2) {
					Float o1m=new Float(o1.getMinValue());
					Float o2m=new Float(o2.getMinValue());
					return o1m.compareTo(o2m);
				}
			});
			StockDaily tmpLow=null;
			for(StockDaily sd:subList){
				if(sd.getMinValue()<=0f){
					continue;
				}else{
					tmpLow=sd;
					break;
				}
			}
			if(tmpLow!=null){
				if(returnDaily.getMinValue()<tmpLow.getMinValue()){
					break;
				}else{
					returnDaily=tmpLow;
				}
			}
		}
		return returnDaily;
	}
	
	private StockDaily getHighIn3InList(StockDaily H0tmp,List<StockDaily> stockList){
		StockDaily returnDaily=H0tmp;
		for(int i=0;i<stockList.size();i=i+3){
			if(i>=stockList.size()){
				break;
			}
			int toIndex;
			if(i+3>=stockList.size()){
				toIndex=stockList.size()-1;
			}else{
				toIndex=i+3;
			}
			List<StockDaily> subList=stockList.subList(i, toIndex);
			Collections.sort(subList, new Comparator<StockDaily>() {

				@Override
				public int compare(StockDaily o1, StockDaily o2) {
					Float o1m=new Float(o1.getMaxValue());
					Float o2m=new Float(o2.getMaxValue());
					return o1m.compareTo(o2m);
				}
			});
			StockDaily tmpHigh=null;
			for(int j=subList.size()-1;j>=0;j--){
				StockDaily sd=subList.get(j);
				if(sd.getMaxValue()<=0f){
					continue;
				}else{
					tmpHigh=sd;
					break;
				}
			}
			if(tmpHigh!=null){
				if(returnDaily.getMaxValue()>tmpHigh.getMaxValue()){
					break;
				}else{
					returnDaily=tmpHigh;
				}
			}
		}
		return returnDaily;
	}
	
	
	
	
	public void test1() throws IOException{
		ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext.xml");
		StockDAO stockDAO=(StockDAO) context.getBean("stockDAO");
		OptDAO optDAO=(OptDAO) context.getBean("optDAO");
		
		Document doc = Jsoup.connect("http://bbs.10jqka.com.cn/codelist.html")
		  .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
		  .proxy("web-proxy.chn.hp.com",8080)
		  .get();
//		Element element=doc.getElementById("sh");
		Element element=doc.getElementById("sz");
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
				stock.setOptId(1);
				stocks.add(stock);
			}
			stockDAO.save(stocks);
		}
	}
}
