//$(document).ready(c());
$(document).ready(function() {
	createGraph();
});

function b(){
	var s1 = [200, 600, 700, 1000];
    var s2 = [460, -210, 690, 820];
    var s3 = [-260, -440, 320, 200];
    // Can specify a custom tick Array.
    // Ticks should match up one for each y value (category) in the series.
    var ticks = [1, 2, 3, 4];
    
    var price = [ [ 1, 6.57 ], [ 2, 6.55 ], [ 3, 6.41 ], [ 4, 6.64 ] ];
    var s=[s1,s2,s3];
    
    var plot1 = $.jqplot('views', [s,price], {
        // The "seriesDefaults" option is an options object that will
        // be applied to all series in the chart.
        seriesDefaults:{
            renderer:$.jqplot.BarRenderer,
            rendererOptions: {fillToZero: true}
        },
        // Custom labels for the series are specified with the "label"
        // option on the series option.  Here a series option object
        // is specified for each series.
        series:[
            {label:'Hotel'},
            {label:'Event Regristration'},
            {label:'Airfare'}
        ],
        // Show the legend and put it outside the grid, but inside the
        // plot container, shrinking the grid to accomodate the legend.
        // A value of "outside" would not shrink the grid and allow
        // the legend to overflow the container.
        legend: {
            show: true,
            placement: 'outsideGrid'
        },
        axes: {
            // Use a category axis on the x axis and use our custom ticks.
            xaxis: {
                renderer: $.jqplot.CategoryAxisRenderer,
                ticks: ticks
            },
            // Pad the y axis just a little so bars can get close to, but
            // not touch, the grid boundaries.  1.2 is the default padding.
            yaxis: {
                pad: 1.05,
                tickOptions: {formatString: '$%d'}
            },
			y2axis : {
				tickOptions : {
					formatString : "$%'d"
				},
				rendererOptions : {
					// align the ticks on the y2 axis with the y axis.
					alignTicks : true,
					forceTickAt0 : true
				}
			}
        }
    });
	
}

function c() {
	var s1 = [ [ 2002, 112000 ], 
	           [ 2003, 122000 ], 
	           [ 2004, 104000 ],
			[ 2005, 99000 ], 
			[ 2006, 121000 ], 
			[ 2007, 148000 ],
			[ 2008, 114000 ], [ 2009, 133000 ], [ 2010, 161000 ],
			[ 2011, 173000 ] ];
	var s2 = [ [ 2002, 10200 ], [ 2003, 10800 ], [ 2004, 11200 ],
			[ 2005, 11800 ], [ 2006, 12400 ], [ 2007, 12800 ], [ 2008, 13200 ],
			[ 2009, 12600 ], [ 2010, 13100 ] ];

	plot1 = $.jqplot("views", [ s2, s1 ], {
		// Turns on animatino for all series in this plot.
		animate : true,
		// Will animate plot on calls to plot1.replot({resetAxes:true})
		animateReplot : true,
		cursor : {
			show : true,
			zoom : true,
			looseZoom : true,
			showTooltip : false
		},
		series : [ {
			pointLabels : {
				show : true
			},
			renderer : $.jqplot.BarRenderer,
			showHighlight : false,
			yaxis : 'y2axis',
			rendererOptions : {
				// Speed up the animation a little bit.
				// This is a number of milliseconds.  
				// Default for bar series is 3000.  
				animation : {
					speed : 2500
				},
				barWidth : 15,
				barPadding : -15,
				barMargin : 0,
				highlightMouseOver : false
			}
		}, {
			rendererOptions : {
				// speed up the animation a little bit.
				// This is a number of milliseconds.
				// Default for a line series is 2500.
				animation : {
					speed : 2000
				}
			}
		} ],
		axesDefaults : {
			pad : 0
		},
		axes : {
			// These options will set up the x axis like a category axis.
			xaxis : {
				tickInterval : 1,
				drawMajorGridlines : false,
				drawMinorGridlines : true,
				drawMajorTickMarks : false,
				rendererOptions : {
					tickInset : 0.5,
					minorTicks : 1
				}
			},
			yaxis : {
				tickOptions : {
					formatString : "$%'d"
				},
				rendererOptions : {
					forceTickAt0 : true
				}
			},
			y2axis : {
				tickOptions : {
					formatString : "$%'d"
				},
				rendererOptions : {
					// align the ticks on the y2 axis with the y axis.
					alignTicks : true,
					forceTickAt0 : true
				}
			}
		},
		highlighter : {
			show : true,
			showLabel : true,
			tooltipAxes : 'y',
			sizeAdjust : 7.5,
			tooltipLocation : 'ne'
		}
	});

}

function createGraph() {
	var zhang = [ 5.63, 7.33, 10.14, 7.83, 5.07, 2.83 ];
	var hui = [ -5.62, -8.82, -5.95, -6.42, -4.55, -5.93 ];
	var gao = [ 1.30, 0.43, 1.42, -1.68, -1.85, -4.20 ];
	var di = [ -0.30, -2.14, 3.59, 0.90, 0.30, -3.27 ];
	var d2 = new Array();
	for(var i=0;i<5;i++){
		var x=2*i;
		d2[x]=new Array();
		d2[x+1]=new Array();
		d2[x][0] =x+1;
		d2[x][1] = gao[i];
		d2[x+1][0] = (x + 2);
		d2[x+1][1] = 0;
	}
	var l = [6.57,6.55 ,6.41, 6.64, 6.7 ];
	var h=[6.94,7.03,7.06,7.16,7.04];
	var price=new Array();
	for(var i=0;i<5;i++){
		var x=2*i;
		price[x]=l[i];
		price[x+1]=h[i];
	}
	//d2 趨勢圖 price 高低線
	var plot = $.jqplot("views", [ price,d2 ], {
		title : "test",
		axes : {

			xaxis : {
				label : "序号",
				tickOptions : {
					isMinorTick : false
				//formatString:'%d'
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
					alignTicks: true
                }
			}
		},
		legend: {
            show: true,
            labels: ["趨勢價格線","高點比率圖"],
            placement: 'outsideGrid'
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

function createGraph2() {
	var zhang = [ 5.63, 7.33, 10.14, 7.83, 5.07, 2.83 ];
	var hui = [ -5.62, -8.82, -5.95, -6.42, -4.55, -5.93 ];
	var gao = [ 1.30, 0.43, 1.42, -1.68, -1.85, -4.20 ];
	var di = [ -0.30, -2.14, 3.59, 0.90, 0.30, -3.27 ];
	var d2 = new Array();
	for(var i=0;i<5;i++){
		var x=2*i;
		d2[x]=new Array();
		d2[x+1]=new Array();
		d2[x][0] =x+1;
		d2[x][1] = gao[i];
		d2[x+1][0] = (x + 2);
		d2[x+1][1] = 0;
	}
	var l = [6.57,6.55 ,6.41, 6.64, 6.7 ];
	var h=[6.94,7.03,7.06,7.16,7.04];
	var price=new Array();
	for(var i=0;i<5;i++){
		var x=2*i;
		price[x]=l[i];
		price[x+1]=h[i];
	}
	//d2 趨勢圖 price 高低線
	var plot = $.jqplot("views", [ d2 ], {
		title : "test",
		axes : {

			xaxis : {
				label : "序号",
				tickOptions : {
					isMinorTick : false
				//formatString:'%d'
				}
			},
			yaxis : {
				suffix : "%",
				tickOptions: {formatString: '%0.2f'}
			}
		},
		
		series : [ 
		    {
				pointLabels: {
	                show: true
	            },
	            renderer: $.jqplot.BarRenderer,
	            showHighlight: false,
	            yaxis: 'yaxis',
	            rendererOptions: {
	                barWidth: 15,
	                fillToZero: true
	            }
			}
		]
	});
}