$(document).ready(function() {
	$("#submitStock").on("click",function(){
		$.post("mvc/getStock",
			{
				stockId:$("#stockId").val()
			},
			function(data){
				var aaa=JSON.parse(data); 
				var arr=aaa.list;
				var l=new Array();
				var h=new Array();
				var dateArr=new Array();
				var landH=new Array();
				for(var i=0;i<arr.length;i++){
					var obj=arr[i];
					dateArr.push(obj.date);
					if(obj.isMin==1){
						l.push(obj.minValue);
						landH[i]=[i+1,obj.minValue];
					}else{
						landH[i]=[i+1,obj.maxValue];
						h.push(obj.maxValue);
					}
				}
				var zhang=getZhang(l,h,dateArr);
				
				createGraph(landH,dateArr,zhang);
        	}
		);
	});
});

var plot;

function createGraph(landH,dateArr,zhang) {
	var ticks=new Array();
	for(var i=0;i<dateArr.length;i++){
		ticks[i]=new Array();
		ticks[i][0]=i+1;
		ticks[i][1]=dateArr[i];
	}
	//d2 趨勢圖 price 高低線
	var plot = $.jqplot("views", [ landH,zhang ], {
		title : "test",
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
            labels: ["趨勢價格線","高點比率圖"]
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

function recreate(){
	 plot.replot();
}

function getZhang(l,h,dateArr){
	var zhang=new Array();
	
	for(var i=0;i<h.length;i++){
		var hx=h[i];
		var lx=l[i];
		var v=(hx-lx)*100/lx;
		zhang.push(parseFloat(v.toFixed(2)));
	}
	
	var d2 = new Array();
	for(var i=0;i<zhang.length;i++){
		var x=2*i;
		d2[x]=new Array();
		d2[x+1]=new Array();
		d2[x][0] =x+1;
		d2[x][1] = zhang[i];
		d2[x+1][0] = (x + 2);
		d2[x+1][1] = 0;
	}
	
	return d2;
}