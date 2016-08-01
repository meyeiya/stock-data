package com.stock.app.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.app.dao.OptDAO;
import com.stock.app.dao.StockDAO;
import com.stock.app.dao.StockDailyDAO;
import com.stock.app.entity.Opt;
import com.stock.app.entity.Stock;
import com.stock.app.entity.StockDaily;
import com.stock.app.util.ToolsUtils;
import com.sun.javafx.binding.StringFormatter;

@Service
@Transactional
public class StockService {

	@Autowired
	private StockDailyDAO stockDailyDAO;
	
	@Autowired
	private StockDAO stockDAO;
	
	@Autowired
	private OptDAO optDAO;
	
	private final String stockdetail = "http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/%s.phtml?year=%s&jidu=%s";
	
	private final String STOCKURL="http://quote.eastmoney.com/stocklist.html";
	
	private final String USERAGENT="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36";
	
	private final boolean useProxy=true;
	
	private final int DEFAULTstartYear=2016;
	private final int DEFAULTstartMonth=1;
	private final int DEFAULTstartJidu=1;
	
	
	public long getStockCount(String... stockIdHead){
		String sql="select count(*) from Stock stock where ";
		for (int i=0;i<stockIdHead.length;i++) {
			if(i>0){
				sql+=" or ";
			}
			sql+=" stock.stockId like '"+stockIdHead[i]+"%'";
		}
		return this.stockDAO.countByQuery(sql);
	}
	
	public void fetchDaily() throws IOException {
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
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
			System.out.println("=======================end saving"+stock.getStockId()+"========================");
		}
//		OptDAO optDAO=(OptDAO) context.getBean("optDAO");
		
		
	}
	
	public List<StockDaily> getAllStockDailyByStockId_Web(String stockId,long startDateLong) throws IOException{

		System.out.println("=======================start saving"+stockId+"========================");
		
		int startYear=DEFAULTstartYear;
		int startMonth=DEFAULTstartMonth;
		int startJidu=DEFAULTstartJidu;
		Date startDate=startDateLong==0l?null:new Date(startDateLong);
		if(startDate!=null){
			Calendar tmp=Calendar.getInstance();
			tmp.setTime(startDate);
			startYear=tmp.get(Calendar.YEAR);
			startMonth=tmp.get(Calendar.MONTH)+1;
			startJidu=ToolsUtils.getQuarter(startMonth);
		}
		Calendar now=Calendar.getInstance();
		int endYear=now.get(Calendar.YEAR);
		int endMonth = now.get(Calendar.MONTH) + 1; 
		int endJidu=ToolsUtils.getQuarter(endMonth);
        
		List<StockDaily> dailyList=new ArrayList<StockDaily>();
		for(int yearI=startYear;yearI<=endYear;yearI++){
			for(int jiduY=startJidu;jiduY<=4;jiduY++){
				if(yearI==endYear&&jiduY>endJidu){
					break;
				}
				String url=StringFormatter.format(stockdetail, stockId,yearI,jiduY).getValue();
				
				Connection connection=Jsoup.connect(url).userAgent(USERAGENT).timeout(0);
				if(useProxy){
					connection.proxy("web-proxy.corp.hp.com", 8080);
				}
				
				Document doc = connection.get();
				
				Element element = doc.getElementById("FundHoldSharesTable");
				if(element==null)
					continue;
				Elements tbodys = element.select("tbody");
				Element tbody = tbodys.get(0);
				Elements trs = tbody.select("tr");
				
				if(trs.size()>0){
					for (int i = trs.size() - 1; i > 0; i--) {
						Element tr = trs.get(i);
						Elements tds = tr.select("td");
						int date_col=1,open_col=2,max_col=3,close_col=4,min_col=5,vol_col=6,mon_col=7;
						Element td = tds.get(date_col-1);
						Element contentE = td.select("div[align='center']>a").get(0);
						
						Calendar tmp=null;
						try {
							tmp=Calendar.getInstance();
							tmp.setTime(convertDate(contentE.text()));
//							if(lastDailyTime!=null){
//								if(tmp.getTimeInMillis()<=lastDailyTime.getTimeInMillis())
//									continue;
//							}
						} catch (ParseException e) {
							e.printStackTrace();
							continue;
						}
						StockDaily daily=new StockDaily();
						daily.setTradeDate(tmp.getTimeInMillis());
						
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
						
						
						daily.setStockId(stockId);
						dailyList.add(daily);
						
					}
//					stockDailyDAO.save(dailyList);
					System.out.println("####################"+stockId+"  "+dailyList.size()+"records");
				}
				
				//***延时***//
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
					
			}
	        
		}
		System.out.println("=======================end saving"+stockId+"========================");
		return dailyList;
	}
	
	public List<StockDaily> getAllStockDailyByStockId_Server(String stockId,Date startDate){
		if(startDate==null){
			return this.stockDailyDAO.findByStockId(stockId);
		}else{
			Calendar tmp=Calendar.getInstance();
			tmp.setTime(startDate);
			long startlong=ToolsUtils.getTimeStart(tmp);
			String sql="from StockDaily daily where daily.stockId=:stockId "
					+ "and daily.tradeDate>=:tradeDate "
					+ "and daily.openValue<>0 order by daily.tradeDate";
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("stockId", stockId);
			params.put("tradeDate", startlong);
			return this.stockDailyDAO.findByQuery(sql, params);
		}
	}
	
	public Opt addNewOpt(){
		Opt opt=new Opt();
		opt.setOptTime(Calendar.getInstance().getTimeInMillis());
		this.optDAO.save(opt);
		return opt;
	}
	
	public void syncSHandSZStock(Opt opt) throws IOException {
		long szcount=this.getStockCount("00","30");
		
		long shcount=this.getStockCount("60");
		
		Connection connection=Jsoup.connect(STOCKURL).userAgent(USERAGENT).timeout(0);
		if(useProxy){
			connection.proxy("web-proxy.corp.hp.com", 8080);
		}
		
		Document doc = connection.get();
		Element element = doc.getElementById("quotesearch");
		Elements sltits=element.select("div.sltit");
		Element shUL=null,szUL=null;
		Map<String,String> shStockMap=new HashMap<String,String>();
		Map<String,String> szStockMap=new HashMap<String,String>();
		
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
					String name=sub.substring(sub.lastIndexOf(">")+1,sub.lastIndexOf("("));
					shStockMap.put(stockId, name);
				}
			}
		}
		if(szUL!=null){
			Elements liElements=szUL.select("li");
			for(int i=0;i<liElements.size();i++){
				String stockId=getStockId(liElements.get(i).html());
				if(stockId.startsWith("00")||stockId.startsWith("30")){
					String sub=liElements.get(i).html().substring(0, liElements.get(i).html().lastIndexOf(stockId));
					String name=sub.substring(sub.lastIndexOf(">")+1,sub.lastIndexOf("("));
					szStockMap.put(stockId, name);
				}
			}
		}
		
		if(shStockMap.size()>shcount){
			String sql="from Stock stock where stock.stockId like '60%'";
			List shStockList=stockDAO.findByQuery(sql);
			for (Object object : shStockList) {
				Stock stock=(Stock) object;
				if(shStockMap.containsKey(stock.getStockId())){
					shStockMap.remove(stock.getStockId());
				}
			}
			List<Stock> patchSaveList=new ArrayList<Stock>();
			for (String stockId : shStockMap.keySet()) {
				Stock newStock=new Stock();
				newStock.setStockId(stockId);
				newStock.setStockName(shStockMap.get(stockId));
				newStock.setOptId(opt.getId());
				patchSaveList.add(newStock);
			}
			this.stockDAO.save(patchSaveList);
		}
		
		if(szStockMap.size()>szcount){
			String sql="from Stock stock where stock.stockId like '30%' or stock.stockId like '00%'";
			List szStockList=stockDAO.findByQuery(sql);
			for (Object object : szStockList) {
				Stock stock=(Stock) object;
				if(szStockMap.containsKey(stock.getStockId())){
					szStockMap.remove(stock.getStockId());
				}
			}
			List<Stock> patchSaveList=new ArrayList<Stock>();
			for (String stockId : szStockMap.keySet()) {
				Stock newStock=new Stock();
				newStock.setStockId(stockId);
				newStock.setStockName(szStockMap.get(stockId));
				newStock.setOptId(opt.getId());
				patchSaveList.add(newStock);
			}
			this.stockDAO.save(patchSaveList);
		}
	}

	public String getStockId(String htmlContent){
        String result=null;
        Pattern pattern = Pattern.compile("(?<=\\()(.+?)(?=\\))");
        Matcher matcher = pattern.matcher(htmlContent);
        while(matcher.find())
        	result=matcher.group();
        return result;
    }
	
	
	
	public float getValue(String str){
		float result=0f;
		try {
			if(str!=null&&str.length()>0)
				result=Float.parseFloat(str);
		} catch (NumberFormatException e) {
		} finally{
			return result;
		}
	}
	
	public boolean isNumeric(String str){ 
		Pattern pattern = Pattern.compile("[0-9]*"); 
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ){
			return false; 
		} 
		return true; 
	}
	
	private Date convertDate(String dateStr) throws ParseException{
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		Calendar tmp=Calendar.getInstance();
		tmp.setTime(dateFormat.parse(dateStr));
		tmp.setTimeInMillis(ToolsUtils.getTimeStart(tmp));
		
		return tmp.getTime();
	}

	public void syncStockDailyData(Opt opt) {
		List list=stockDAO.findAll();
		try {
			for (Object object : list) {
				Stock stock=(Stock) object;
				syncDailyData(opt,stock);
				//test
//				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void syncDailyData(Opt opt,Stock stock) throws IOException{
		String sql="from StockDaily daily where daily.stockId=:stockId1 and daily.tradeDate=(select max(tmp.tradeDate) from StockDaily tmp where tmp.stockId=:stockId2)";
		Map<String, Object> args=new HashMap<String, Object>();
		args.put("stockId1", stock.getStockId());
		args.put("stockId2", stock.getStockId());
		List<StockDaily> list=this.stockDailyDAO.findByQuery(sql,args);
		if(list!=null&&list.size()>0){
			StockDaily stockDaily=list.get(0);
			Calendar lastDailyTime=Calendar.getInstance();
			lastDailyTime.setTimeInMillis(stockDaily.getTradeDate());
			
			getDialyData(lastDailyTime,opt,stock.getStockId());
		}else{
			getDialyData(null,opt,stock.getStockId());
		}
	}
	
	private void getDialyData(Calendar lastDailyTime,Opt opt,String stockId) throws IOException{
		System.out.println("=======================start saving"+stockId+"========================");
		
		Calendar now=Calendar.getInstance();
		now.setTimeInMillis(opt.getOptTime());
		now.setTimeInMillis(ToolsUtils.getTimeStart(now));
		
		int startYear=DEFAULTstartYear;
		int startMonth=DEFAULTstartMonth;
		int startJidu=DEFAULTstartJidu;
		if(lastDailyTime!=null){
			if(lastDailyTime.before(now)){
				startYear=lastDailyTime.get(Calendar.YEAR);
				startMonth=now.get(Calendar.MONTH)+1;
				startJidu=ToolsUtils.getQuarter(startMonth);
			}else{
				return;
			}
		}
		int endYear=now.get(Calendar.YEAR);
		int endMonth = now.get(Calendar.MONTH) + 1; 
		int endJidu=ToolsUtils.getQuarter(endMonth);
        
		for(int yearI=startYear;yearI<=endYear;yearI++){
			for(int jiduY=startJidu;jiduY<=4;jiduY++){
				if(yearI==endYear&&jiduY>endJidu){
					break;
				}
				String url=StringFormatter.format(stockdetail, stockId,yearI,jiduY).getValue();
				
				Connection connection=Jsoup.connect(url).userAgent(USERAGENT).timeout(0);
				if(useProxy){
					connection.proxy("web-proxy.corp.hp.com", 8080);
				}
				
				Document doc = connection.get();
				
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
						int date_col=1,open_col=2,max_col=3,close_col=4,min_col=5,vol_col=6,mon_col=7;
						Element td = tds.get(date_col-1);
						Element contentE = td.select("div[align='center']>a").get(0);
						
						Calendar tmp=null;
						try {
							tmp=Calendar.getInstance();
							tmp.setTime(convertDate(contentE.text()));
							if(lastDailyTime!=null){
								if(tmp.getTimeInMillis()<=lastDailyTime.getTimeInMillis())
									continue;
							}
						} catch (ParseException e) {
							e.printStackTrace();
							continue;
						}
						StockDaily daily=new StockDaily();
						daily.setTradeDate(tmp.getTimeInMillis());
						
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
						
						
						daily.setStockId(stockId);
						dailyList.add(daily);
						
					}
					stockDailyDAO.save(dailyList);
					System.out.println("####################"+stockId+"  "+dailyList.size()+"records");
				}
				
				//***延时***//
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
					
			}
	        
		}
		System.out.println("=======================end saving"+stockId+"========================");
	}
}
