function toDDMMMYYYY(date) {  
    var d = new Date(date.getTime());  
    var dd = d.getDate() < 10 ? "0" + d.getDate() : d.getDate().toString();  
    var mmm = mths[d.getMonth()];  
    var yyyy = d.getFullYear().toString(); //2011  
        //var YY = YYYY.substr(2);   // 11  
    return dd + mmm + yyyy;  
}  

$(document).ready(function() {
	$("#dateInput").jqxDateTimeInput({ width: '200px', height: '25px' });
	
	$("#submitStock").on("click",function(){
		
		var isCheck=$("#needStartTime").prop("checked")
		var inputDate=null;
		if(isCheck)
			inputDate=$("#dateInput").jqxDateTimeInput('value');
		var param=inputDate!=null? inputDate.getTime():0;
		
		$.post("mvc/getStock",
			{
				stockId:$("#stockId").val(),
				startDate:param
			},
			function(data){
//				var daTest={"list":[{"date":"01-14","isMin":1,"minValue":13.58,"tradeVol":7170526,"tradeSum":101053992},{"date":"01-21","isMin":0,"maxValue":16.28,"tradeVol":6568886,"tradeSum":104273552},{"date":"01-29","isMin":1,"minValue":12.71,"tradeVol":6301003,"tradeSum":82963160},{"date":"02-24","isMin":0,"maxValue":16.3,"tradeVol":6806345,"tradeSum":109330520},{"date":"03-01","isMin":1,"minValue":13.24,"tradeVol":6187214,"tradeSum":83465312},{"date":"03-09","isMin":0,"maxValue":15.7,"tradeVol":6526583,"tradeSum":99769640},{"date":"03-11","isMin":1,"minValue":13.7,"tradeVol":3089104,"tradeSum":43291420},{"date":"03-25","isMin":0,"maxValue":21.14,"tradeVol":37660532,"tradeSum":738905024},{"date":"04-08","isMin":1,"minValue":16.51,"tradeVol":6060438,"tradeSum":101125672},{"date":"04-13","isMin":0,"maxValue":17.97,"tradeVol":10744259,"tradeSum":186576816},{"date":"04-22","isMin":1,"minValue":14.37,"tradeVol":4572334,"tradeSum":67374832},{"date":"05-06","isMin":0,"maxValue":15.57,"tradeVol":5476184,"tradeSum":82564600},{"date":"05-12","isMin":1,"minValue":12.9,"tradeVol":3062152,"tradeSum":40268582},{"date":"05-17","isMin":0,"maxValue":13.94,"tradeVol":2855878,"tradeSum":39140498},{"date":"05-26","isMin":1,"minValue":12.8,"tradeVol":2871136,"tradeSum":37480625},{"date":"06-03","isMin":0,"maxValue":14.67,"tradeVol":4509867,"tradeSum":65071805},{"date":"06-13","isMin":1,"minValue":13.01,"tradeVol":8712961,"tradeSum":119567919},{"date":"06-24","isMin":0,"maxValue":16.12,"tradeVol":22245862,"tradeSum":345888567},{"date":"06-30","isMin":1,"minValue":14.91,"tradeVol":6698076,"tradeSum":101211740},{"date":"07-05","isMin":0,"maxValue":17.0,"tradeVol":14995659,"tradeSum":244244955},{"date":"07-06","isMin":1,"minValue":15.7,"tradeVol":9214660,"tradeSum":148067749},{"date":"07-15","isMin":0,"maxValue":16.9,"tradeVol":10741226,"tradeSum":176913098},{"date":"07-18","isMin":1,"minValue":15.68,"tradeVol":7602391,"tradeSum":122322326},{"date":"07-25","isMin":0,"maxValue":17.21,"tradeVol":7509718,"tradeSum":126533622},{"date":"08-01","isMin":1,"minValue":14.86,"tradeVol":3434454,"tradeSum":51696013},{"date":"08-08","isMin":0,"maxValue":16.28,"tradeVol":3650870,"tradeSum":58357710},{"date":"08-11","isMin":1,"minValue":15.5,"tradeVol":2548135,"tradeSum":39904901},{"date":"08-16","isMin":0,"maxValue":16.26,"tradeVol":3811974,"tradeSum":61466616},{"date":"08-22","isMin":1,"minValue":15.85,"tradeVol":3283043,"tradeSum":52555405},{"date":"08-24","isMin":0,"maxValue":17.7,"tradeVol":10577158,"tradeSum":183708539}],"queryTypeList_1":"19.88,28.25,18.58,54.31,8.84,8.35,8.06,14.61,23.9,14.02,7.64,9.76,9.56,4.9,11.67","queryTypeList_2":"-21.93,-18.77,-12.74,-21.9,-20.03,-17.15,-8.18,-11.32,-7.51,-7.65,-7.22,-13.65,-4.79,-2.52","queryTypeList_3":"0.12,-3.68,34.65,-15.0,-13.36,-10.47,5.24,9.88,5.46,-0.59,1.83,-5.4,-0.12,8.86","queryTypeList_4":"-6.41,4.17,3.47,20.51,-12.96,-10.23,-0.78,1.64,14.6,5.3,-0.13,-5.23,4.31,2.26"};
				//var aaa=daTest;
				var aaa=JSON.parse(data); 
				var arr=aaa.list;
				var queryTypeList_1=aaa.queryTypeList_1;
				var queryTypeList_2=aaa.queryTypeList_2;
				var queryTypeList_3=aaa.queryTypeList_3;
				var queryTypeList_4=aaa.queryTypeList_4;
				var l=new Array();
				var h=new Array();
				var landH=new Array();
				
				var dateArr=new Array();
				var htmlLength=arr.length/2;
				var html="<table><tr><td></td>";
				for(var i=0;i<htmlLength;i++){
					html+="<td>L"+i+"</td>";
					html+="<td>H"+i+"</td>";
				}
				html+="</tr><tr>";
				
				var line1="<td><span>交易日</span></td>",line2="<td><span>最低/高价</span></td>",line3="<td><span>交易量</span></td>",line4="<td><span>交易额</span></td>";
				for(var i=0;i<arr.length;i++){
					var obj=arr[i];
					dateArr.push(obj.date);
					line1+="<td>";
					line1+="<span>"+obj.date+"</span>";
					line1+="</td>";
					if(obj.isMin==1){
						line2+="<td>";
						line2+="<span>"+obj.minValue+"</span>";
						line2+="</td>";
						l.push(obj.minValue);
						landH[i]=[i+1,obj.minValue];
					}else{
						line2+="<td>";
						line2+="<span>"+obj.maxValue+"</span>";
						line2+="</td>";
						landH[i]=[i+1,obj.maxValue];
						h.push(obj.maxValue);
					}
					line3+="<td>";
					line3+="<span>"+obj.tradeVol+"</span>";
					line3+="</td>";
					
					line4+="<td>";
					line4+="<span>"+obj.tradeSum+"</span>";
					line4+="</td>";
				}
				
				html+=line1;
				html+="</tr><tr>";
				html+=line2;
				html+="</tr><tr>";
				html+=line3;
				html+="</tr><tr>";
				html+=line4;
				
				html+="</tr><table>";
				$("#stockInfo1").empty().append(html);
				
				var columnData1=getColumnData(queryTypeList_1,1);
				var columnData2=getColumnData(queryTypeList_2,2);
				var columnData3=getColumnData(queryTypeList_3,3);
				var columnData4=getColumnData(queryTypeList_4,4);
				
				createGraph(landH,dateArr,columnData1,1);
				createGraph(landH,dateArr,columnData2,2);
				createGraph(landH,dateArr,columnData3,3);
				createGraph(landH,dateArr,columnData4,4);
				
				adjust_y2axis_css();
        	}
		);
	});
	
	
	$("#syncStock").on("click",function(){
		$.post("mvc/sync",
				function(data){
					alert(data); 
    			}
		);
	});
});

function adjust_y2axis_css(){
	$(".jqplot-series-1").each(function(){
		if(parseFloat($(this).text())!=0){
			$(this).addClass("hasValue");
		}else{
			$(this).addClass("noneValue");
		}
	});
}

var plot;

function createGraph(landH,dateArr,zhang,typeVal) {
	
	var ticks=new Array();
	for(var i=0;i<dateArr.length;i++){
		ticks[i]=new Array();
		ticks[i][0]=i+1;
		ticks[i][1]=dateArr[i];
	}
	var labels=new Array();
	var viewId;
	labels[0]="高低点价格线";
	if(typeVal==1){
		labels[1]="趋势涨幅图";
		viewId="views1";
	}else if(typeVal==2){
		labels[1]="趋势回撤图";
		viewId="views2";
	}else if(typeVal==3){
		labels[1]="高点比率图";
		viewId="views3";
	}else if(typeVal==4){
		labels[1]="低点比率图";
		viewId="views4";
	}
	
	$("#"+viewId).empty();
	//d2 趨勢圖 price 高低線
	var plot = $.jqplot(viewId, [ landH,zhang ], {
		title : "股票分析——"+labels[1],
		axes : {

			xaxis : {
				label : "日期",
				ticks: ticks,
				tickOptions : {
					isMinorTick : false
				},
				rendererOptions: {
	                tickInset: 0.5,
	                minorTicks: 1
	            }
			},
			yaxis : {
				suffix : "%",
				tickOptions: {formatString: '%0.2f'},
				rendererOptions: {
                }
			},
			y2axis : {
				tickOptions: {formatString: '%0.2f'},
                rendererOptions: {
                    // align the ticks on the y2 axis with the y axis.
                    alignTicks: true,
                    forceTickAt0: true
                }
			}
		},
		legend: {
            show: true,
            labels: labels
        },
		series : [ 
			{
				pointLabels: {
			        show: true
			    },
			    renderer: $.jqplot.LineRenderer,
			    showHighlight: false,
			    yaxis: 'yaxis'
			
			},
		    {
				
				pointLabels: {
	                show: true
	            },
	            renderer: $.jqplot.BarRenderer,
	            showHighlight: false,
	            yaxis: 'y2axis',
	            rendererOptions: {
	                barWidth: 15,
	                fillToZero: true
	            }
			}
			
		]
	});
	
	
}

function getColumnData(queryTypeList,typeVal){
	var columnArr=queryTypeList.split(",");
	
	var d2 = new Array();
	for(var i=0;i<columnArr.length;i++){
		var x=2*i;
		d2[x]=new Array();
		d2[x+1]=new Array();
		d2[x][0] =x+1;
		d2[x+1][0] = x + 2;
		
		if(typeVal==2){
			d2[x+1][1] = parseFloat(columnArr[i]);
			d2[x][1] = 0;
		}else if(typeVal==3){
			if(i==0){
				d2[x][1] = 0;
				d2[x+1][1] = 0;
			}else{
				d2[x+1][1] = parseFloat(columnArr[i-1]);
				d2[x][1] = 0;
			}
		}else if(typeVal==4){
			if(i==0){
				d2[x][1] = 0;
				d2[x+1][1] = 0;
			}else{
				d2[x][1] = parseFloat(columnArr[i-1]);
				d2[x+1][1] = 0;
			}
		}else{
			d2[x][1] = parseFloat(columnArr[i]);
			d2[x+1][1] = 0;
		}
	}
	
	return d2;
}