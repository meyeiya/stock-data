package com.stock.app.mvc;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.stock.app.entity.Opt;
import com.stock.app.entity.StockDaily;
import com.stock.app.service.StockService;
import com.stock.app.util.StockQueryType;

@Controller
@RequestMapping("/mvc")
public class MVCController {

	
	//http://quote.eastmoney.com/stocklist.html
	@Autowired
	private StockService stockService;
	
	private final SimpleDateFormat dateFormat=new SimpleDateFormat("MM-dd");
	
	@RequestMapping("/sync")
    public void syncData(PrintWriter pw){
		Opt opt=stockService.addNewOpt();
		try {
			stockService.syncSHandSZStock(opt);
		} catch (IOException e) {
			e.printStackTrace();
		}
		stockService.syncStockDailyData(opt);
		pw.write("1");
	}
	
	
	@RequestMapping("/getStock")
    public void getStock(String stockId,int queryType,long startDate,PrintWriter pw){
		List<StockDaily> stockDailies = null;
		try {
			stockDailies = this.stockService.getAllStockDailyByStockId_Web(stockId,startDate);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collections.sort(stockDailies, new Comparator<StockDaily>() {

			@Override
			public int compare(StockDaily o1, StockDaily o2) {
				return o1.getTradeDate().compareTo(o2.getTradeDate());
			}
		});
		List<StockDaily> lowList=new ArrayList<StockDaily>();
		List<StockDaily> highList=new ArrayList<StockDaily>();
		if(stockDailies!=null&&stockDailies.size()>0){
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
					break;
				}
				ArrayList<StockDaily> tmpList=new ArrayList<StockDaily>(stockDailies.subList(index+1, stockDailies.size()));
				StockDaily lowIn3=getLowIn3InList(Lx,(List)tmpList.clone());
				lowList.add(lowIn3);
				
				index=getIndex(stockDailies,lowIn3);
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
					break;
				}
				tmpList=new ArrayList<StockDaily>(stockDailies.subList(index+1, stockDailies.size()));
				StockDaily highIn3=getHighIn3InList(Hx,(List)tmpList.clone());
				highList.add(highIn3);
				
				index=getIndex(stockDailies,highIn3);
				if(index+1>=stockDailies.size()){
					break;
				}
				index++;
			}

		}
		
		JsonObject jsonObject=new JsonObject();
		
		List<Float> highArr=new ArrayList<Float>();
		
		for (int i = 0; i < highList.size(); i++) {
			StockDaily stockDaily=highList.get(i);
			highArr.add(stockDaily.getMaxValue());
		}
		
		JsonArray jsonArray=new JsonArray();
		
		Calendar c=Calendar.getInstance();
		for (int i = 0; i < lowList.size(); i++) {
			JsonObject object1=new JsonObject();
			StockDaily low=lowList.get(i);
			c.setTimeInMillis(low.getTradeDate());
			object1.addProperty("date", dateFormat.format(c.getTime()));
			object1.addProperty("isMin", 1);
			object1.addProperty("minValue", low.getMinValue());
			object1.addProperty("tradeVol", low.getTradeVol());
			object1.addProperty("tradeSum", low.getTradeSum());
			jsonArray.add(object1);
			if(i<highList.size()){
				JsonObject object2=new JsonObject();
				StockDaily high=highList.get(i);
				c.setTimeInMillis(high.getTradeDate());
				object2.addProperty("date", dateFormat.format(c.getTime()));
				object2.addProperty("isMin", 0);
				object2.addProperty("maxValue", high.getMaxValue());
				object2.addProperty("tradeVol", high.getTradeVol());
				object2.addProperty("tradeSum", high.getTradeSum());
				jsonArray.add(object2);
			}
		}
		List queryTypeList=getQueryTypeList(queryType,lowList,highList);
		
		jsonObject.add("list",jsonArray);
		jsonObject.addProperty("queryTypeList", StringUtils.join(queryTypeList, ","));
        pw.write(jsonObject.toString());
    }
	
	private int getIndex(List<StockDaily> stockDailies, StockDaily obj) {
		int index=0;
		for (int i = 0; i < stockDailies.size(); i++) {
			if(stockDailies.get(i).getTradeDate()==obj.getTradeDate()
					&&stockDailies.get(i).getStockId().equals(obj.getStockId())){
				break;
			}
			index++;
		}
		return index;
	}


	private List getQueryTypeList(int queryType, List<StockDaily> lowList, List<StockDaily> highList) {
		List<Float> returnList=new ArrayList<Float>();
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		if(queryType==StockQueryType.TRENDUP.getType()){
			for(int i=0;i<highList.size();i++){
				StockDaily lowx=lowList.get(i);
				StockDaily highx=highList.get(i);
				// H1/L1-1
				returnList.add(
						Float.parseFloat(
								decimalFormat.format((highx.getMaxValue()-lowx.getMinValue())*100/lowx.getMinValue())));
			}
		}else if(queryType==StockQueryType.TRENDDOWN.getType()){
			if(lowList.size()>1){
				for(int i=1;i<lowList.size();i++){
					StockDaily lowx=lowList.get(i);
					StockDaily highx=highList.get(i-1);
					// L2/H1-1
					returnList.add(
							Float.parseFloat(
									decimalFormat.format((lowx.getMinValue()-highx.getMaxValue())*100/highx.getMaxValue())));
				}
			}
		}else if(queryType==StockQueryType.TOPRATE.getType()){
			if(highList.size()>1){
				for(int i=1;i<highList.size();i++){
					StockDaily highx=highList.get(i);
					StockDaily highx_1=highList.get(i-1);
					//	H2/H1-1
					returnList.add(
							Float.parseFloat(
									decimalFormat.format((highx.getMaxValue()-highx_1.getMaxValue())*100/highx_1.getMaxValue())));
				}
			}
		}else if(queryType==StockQueryType.BOTTOMRATE.getType()){
			if(lowList.size()>1){
				for(int i=1;i<lowList.size();i++){
					StockDaily lowx=lowList.get(i);
					StockDaily lowx_1=lowList.get(i-1);
					//	H2/H1-1
					returnList.add(
							Float.parseFloat(
									decimalFormat.format((lowx.getMinValue()-lowx_1.getMinValue())*100/lowx_1.getMinValue())));
				}
			}
		}
		return returnList;
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
	
	@RequestMapping("/load")
    public void load(PrintWriter pw){
		try {
			stockService.fetchDaily();
		} catch (IOException e) {
			e.printStackTrace();
		}
        pw.write("ok");
    }
	
	

}
