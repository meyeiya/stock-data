package com.stock.app.mvc;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.stock.app.entity.StockDaily;
import com.stock.app.service.StockService;

@Controller
@RequestMapping("/mvc")
public class MVCController {

	@Autowired
	private StockService stockService;
	
	@RequestMapping("/getStock")
    public void getStock(String stockId,PrintWriter pw){
		List<StockDaily> stockDailies=this.stockService.getAllStockDailyByStockId(stockId);
		List<StockDaily> lowList=new ArrayList<StockDaily>();
		List<StockDaily> highList=new ArrayList<StockDaily>();
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
		
		JsonObject jsonObject=new JsonObject();
		
		List<Float> highArr=new ArrayList<Float>();
		
		for (int i = 0; i < highList.size(); i++) {
			StockDaily stockDaily=highList.get(i);
			highArr.add(stockDaily.getMaxValue());
		}
		List<Float> lowArr=new ArrayList<Float>();
		
		
		JsonArray jsonArray=new JsonArray();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		Calendar c=Calendar.getInstance();
		for (int i = 0; i < lowList.size(); i++) {
			JsonObject object1=new JsonObject();
			StockDaily low=lowList.get(i);
			c.setTimeInMillis(low.getTradeDate());
			object1.addProperty("date", dateFormat.format(c.getTime()));
			object1.addProperty("isMin", 1);
			object1.addProperty("minValue", low.getMinValue());
			jsonArray.add(object1);
			if(i<highList.size()){
				JsonObject object2=new JsonObject();
				StockDaily high=highList.get(i);
				c.setTimeInMillis(high.getTradeDate());
				object2.addProperty("date", dateFormat.format(c.getTime()));
				object2.addProperty("isMin", 0);
				object2.addProperty("maxValue", high.getMaxValue());
				jsonArray.add(object2);
			}
		}
		jsonObject.add("list",jsonArray);
//		jsonObject.addProperty("lowList", StringUtils.join(lowArr, ","));
//		jsonObject.addProperty("highList", StringUtils.join(highArr, ","));
        pw.write(jsonObject.toString());
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
