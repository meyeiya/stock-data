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
				queryType:parseInt($("#typeSelect").val()),
				startDate:param
			},
			function(data){
				var aaa=JSON.parse(data); 
				var arr=aaa.list;
				var queryTypeList=aaa.queryTypeList;
				var l=new Array();
				var h=new Array();
				var dateArr=new Array();
				var landH=new Array();
				
				var htmlLength=arr.length/2;
				var html="<table><tr>";
				for(var i=0;i<htmlLength;i++){
					html+="<td>L"+i+"</td>";
					html+="<td>H"+i+"</td>";
				}
				html+="</tr><tr>";
				
				for(var i=0;i<arr.length;i++){
					var obj=arr[i];
					dateArr.push(obj.date);
					html+="<td>";
					html+="<span>"+obj.date+"</span>";
					if(obj.isMin==1){
						html+="<span>"+obj.minValue+"</span>";
						l.push(obj.minValue);
						landH[i]=[i+1,obj.minValue];
					}else{
						html+="<span>"+obj.maxValue+"</span>";
						landH[i]=[i+1,obj.maxValue];
						h.push(obj.maxValue);
					}
					
					html+="</td>";
				}
				
				html+="</tr><table>";
				$("#stockInfo1").empty().append(html);
				
				var columnData=getColumnData(queryTypeList);
				
				createGraph(landH,dateArr,columnData);
				
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
		if(parseFloat($(this).text())>0){
			$(this).addClass("hasValue");
		}else{
			$(this).addClass("noneValue");
		}
	});
}

var plot;

function createGraph(landH,dateArr,zhang) {
	var ticks=new Array();
	for(var i=0;i<dateArr.length;i++){
		ticks[i]=new Array();
		ticks[i][0]=i+1;
		ticks[i][1]=dateArr[i];
	}
	var labels=new Array();
	labels[0]="高低点价格线";
	var typeVal=parseInt($("#typeSelect").val());
	if(typeVal==1){
		labels[1]="趋势涨幅图";
	}else if(typeVal==2){
		labels[1]="趋势回撤图";
	}else if(typeVal==3){
		labels[1]="高点比率图";
	}else if(typeVal==4){
		labels[1]="低点比率图";
	}
	//d2 趨勢圖 price 高低線
	var plot = $.jqplot("views", [ landH,zhang ], {
		title : "股票分析",
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

function getColumnData(queryTypeList){
	var columnArr=queryTypeList.split(",");
	
	
	var d2 = new Array();
	for(var i=0;i<columnArr.length;i++){
		var x=2*i;
		d2[x]=new Array();
		d2[x+1]=new Array();
		d2[x][0] =x+1;
		d2[x][1] = parseFloat(columnArr[i]);
		d2[x+1][0] = (x + 2);
		d2[x+1][1] = 0;
	}
	
	return d2;
}