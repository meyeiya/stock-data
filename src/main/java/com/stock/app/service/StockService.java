package com.stock.app.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.app.dao.StockDAO;
import com.stock.app.dao.StockDailyDAO;
import com.stock.app.entity.Stock;
import com.stock.app.entity.StockDaily;
import com.sun.javafx.binding.StringFormatter;

@Service
@Transactional
public class StockService {

	@Autowired
	private StockDailyDAO stockDailyDAO;
	
	@Autowired
	private StockDAO stockDAO;
	
	private final String stockdetail = "http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/%s.phtml?year=%s&jidu=%s";
	
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
	
	public List<StockDaily> getAllStockDailyByStockId(String stockId){
		return this.stockDailyDAO.findByStockId(stockId);
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
		return dateFormat.parse(dateStr);
	}
}
