import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.RectangleEdge;

import java.awt.GridLayout;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.JRadioButton;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;

public class CandleStick extends JFrame implements ChartMouseListener{

	private JPanel contentPane;
	private String symbolName;

	JPanel TimeSeriesSelectionPanel;
	private ChartPanel chartPanel2;
	private JFreeChart chart2;
	private XYPlot plot2;
	private Crosshair xCrosshair2;
    private Crosshair yCrosshair2;
    private static boolean firstSelected = true;
    private ButtonGroup timeSeriesGroup;
    private JRadioButton rdbtn5Min;
    private JRadioButton rdbtn15Min;
    private JRadioButton rdbtn30Min;
    private JRadioButton rdbtn60Min;
    private JRadioButton rdbtnDaily;
    private JRadioButton rdbtnWeekly;
    private JRadioButton rdbtnMonthly;
    private JButton btnClear;
    
    /*	Add username to Portfolio (top right)
     *  Move stock icon to top left
     *  
     *  
     *  Password should not include username
     *  
     * */
    
    
    
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CandleStick frame = new CandleStick();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	public CandleStick() {
		this("NA");
	}
	public CandleStick(String symbolName) {
		super();
		this.symbolName = symbolName;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    this.setExtendedState(this.getExtendedState() | this.MAXIMIZED_BOTH);
		setBounds(100, 100, 755, 535);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		TimeSeriesSelectionPanel = new JPanel();
		contentPane.add(TimeSeriesSelectionPanel, BorderLayout.SOUTH);
		TimeSeriesSelectionPanel.setLayout(new GridLayout(4, 2, 10, 10));
		
		// set up buttons
		rdbtn5Min = new JRadioButton("5 Minute");
		rdbtn5Min.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				try {
				clearPlot();
				createContent(StockChartData.TimeFrame.INTRADAY, symbolName, StockChartData.Interval.FIVE);
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(null, "Error in retrieving chart: INTRADAY charts are currently unavailable.");
				}
			}
		});
		TimeSeriesSelectionPanel.add(rdbtn5Min);
		
		rdbtn30Min = new JRadioButton("30 Minute");
		rdbtn30Min.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				try {
				clearPlot();
				createContent(StockChartData.TimeFrame.INTRADAY, symbolName, StockChartData.Interval.THIRTY);
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(null, "Error in retrieving chart: INTRADAY charts are currently unavailable.");
				}
			}
		});
		TimeSeriesSelectionPanel.add(rdbtn30Min);
		
		rdbtn15Min = new JRadioButton("15 Minute");
		rdbtn15Min.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				try {
				clearPlot();
				createContent(StockChartData.TimeFrame.INTRADAY, symbolName, StockChartData.Interval.FIFTEEN);
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(null, "Error in retrieving chart: INTRADAY charts are currently unavailable.");
				}
			}
		});
		TimeSeriesSelectionPanel.add(rdbtn15Min);
		
		rdbtn60Min = new JRadioButton("60 Minute");
		rdbtn60Min.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				try {
				clearPlot();
				createContent(StockChartData.TimeFrame.INTRADAY, symbolName, StockChartData.Interval.SIXTY);
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(null, "Error in retrieving chart: Intraday charts are currently unavailable.");
				}
			}
		});
		TimeSeriesSelectionPanel.add(rdbtn60Min);
		
		rdbtnDaily = new JRadioButton("Daily");
		rdbtnDaily.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				try {
				clearPlot();
				createContent(StockChartData.TimeFrame.DAILY, symbolName, StockChartData.Interval.FIVE);
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(null, "An error occured in displaying the daily chart.");
				}
			}
		});
		TimeSeriesSelectionPanel.add(rdbtnDaily);	
		
		rdbtnWeekly = new JRadioButton("Weekly");
		rdbtnWeekly.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				try {
				clearPlot();
				createContent(StockChartData.TimeFrame.WEEKLY, symbolName, StockChartData.Interval.FIVE);
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(null, "An error occured in displaying the weekly chart.");
				}
			}
		});
		TimeSeriesSelectionPanel.add(rdbtnWeekly);
		
		rdbtnMonthly = new JRadioButton("Monthly");
		rdbtnMonthly.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				try {
				clearPlot();
				createContent(StockChartData.TimeFrame.MONTHLY, symbolName, StockChartData.Interval.FIVE);
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(null, "An error occured in displaying the monthly chart.");
				}
			}
		});
		TimeSeriesSelectionPanel.add(rdbtnMonthly);
		
		timeSeriesGroup = new ButtonGroup();
		timeSeriesGroup.add(rdbtn5Min);
		timeSeriesGroup.add(rdbtn15Min);
		timeSeriesGroup.add(rdbtn30Min);
		timeSeriesGroup.add(rdbtn60Min);
		timeSeriesGroup.add(rdbtnDaily);
		timeSeriesGroup.add(rdbtnWeekly);
		timeSeriesGroup.add(rdbtnMonthly);
				
		// set Daily as selected first
		rdbtnDaily.setSelected(true);
			
		}
		
		// Set up item listeners to recall constructor when radio button selections are changed
//	    rdbtn5Min.addItemListener(new HandlerClass(StockChartData.TimeFrame.INTRADAY, symbolName, StockChartData.Interval.FIVE));
//	    rdbtn15Min.addItemListener(new HandlerClass(StockChartData.TimeFrame.INTRADAY, symbolName, StockChartData.Interval.FIFTEEN));
//	    rdbtn30Min.addItemListener(new HandlerClass(StockChartData.TimeFrame.INTRADAY, symbolName, StockChartData.Interval.THIRTY));
//	    rdbtn60Min.addItemListener(new HandlerClass(StockChartData.TimeFrame.INTRADAY, symbolName, StockChartData.Interval.SIXTY));
//	    rdbtnDaily.addItemListener(new HandlerClass(StockChartData.TimeFrame.DAILY, symbolName, StockChartData.Interval.FIVE));
//	    rdbtnWeekly.addItemListener(new HandlerClass(StockChartData.TimeFrame.WEEKLY, symbolName, StockChartData.Interval.FIVE));
//	    rdbtnMonthly.addItemListener(new HandlerClass(StockChartData.TimeFrame.MONTHLY, symbolName, StockChartData.Interval.FIVE));
//		
	
	// The following functions will aid in displaying the candlestick chart on to the chartPanel
	
			public JPanel getContentPane() {
		return contentPane;
	}

			public void createContent(StockChartData.TimeFrame timeF, String symbol, StockChartData.Interval interval) {
				try {					
					/*Grab the stock data from the Alpha Vantage API*/
					StockChartData stockData= new StockChartData(timeF, symbol, interval);
					final int size = stockData.getOpens().size();
					OHLCDataItem[] data = new OHLCDataItem[size];
					for(Date date : stockData.getDates()) {
						date.setYear(date.getYear()-1900); // edit years
						date.setMonth(date.getMonth()-1);
					}
					for(int i = 0; i < size; i++) {
						data[i] = new OHLCDataItem(stockData.getDates().pop(), stockData.getOpens().pop(), stockData.getHighs().pop(), stockData.getLows().pop(), stockData.getCloses().pop(), stockData.getVolumes().pop());
					}
				        
		        CandlestickRenderer renderer = new CandlestickRenderer();
		        DefaultOHLCDataset  dataSet  = new DefaultOHLCDataset(1, data);
		        chart2 = ChartFactory.createCandlestickChart(symbol, "Date", "Price", (OHLCDataset) dataSet, false);
		        plot2 = chart2.getXYPlot(); 
		        DateAxis domainAxis = (DateAxis) plot2.getDomainAxis();
		        NumberAxis rangeAxis = new NumberAxis();
		        renderer.setSeriesPaint(0, Color.BLACK);
		        renderer.setDrawVolume(true);
		        DateTickUnit tickUnit = new DateTickUnit(DateTickUnit.DAY, 1);
		        SimpleDateFormat customDateFormat = new SimpleDateFormat("MMM");;
		        // Set dateFormat depending on TimeSeries
		        String dateFormat = "MMM-dd";;
		        if(stockData.getTimeFrame() == StockChartData.TimeFrame.MONTHLY) {
		        	tickUnit = new DateTickUnit(DateTickUnit.MONTH, 9);
		        	dateFormat = "yyyy-MMM";
		        	customDateFormat = new SimpleDateFormat(dateFormat);
		        }
		        else if(stockData.getTimeFrame() == StockChartData.TimeFrame.WEEKLY) {
		        	tickUnit = new DateTickUnit(DateTickUnit.MONTH, 8);
		        	dateFormat = "MMM-YYYY";
		        	customDateFormat = new SimpleDateFormat(dateFormat);
		            }
		        else if(stockData.getTimeFrame() == StockChartData.TimeFrame.DAILY){
		        	tickUnit = new DateTickUnit(DateTickUnit.DAY, 20);
		        	dateFormat = "MMM-dd";
		        	customDateFormat = new SimpleDateFormat(dateFormat);
		        	
		        }
		        else if(stockData.getTimeFrame() == StockChartData.TimeFrame.INTRADAY) { // set Intraday custom dateformats and tick units
		        	switch(stockData.getInterval()) {
		        	case ONE:		tickUnit = new DateTickUnit(DateTickUnit.MINUTE, 10);
		        					dateFormat = "HH:mm:ss";
		        					customDateFormat = new SimpleDateFormat(dateFormat);
		        					break;
		        					
		        	case FIVE:		tickUnit = new DateTickUnit(DateTickUnit.MINUTE, 30);
									dateFormat = "HH:mm";
									customDateFormat = new SimpleDateFormat(dateFormat);
									break;
					
		        	case FIFTEEN:	tickUnit = new DateTickUnit(DateTickUnit.HOUR, 2);
									dateFormat = "EEE, HH:mm";
									customDateFormat = new SimpleDateFormat(dateFormat);
									break;
					
		        	case THIRTY:	tickUnit = new DateTickUnit(DateTickUnit.HOUR, 6);
									dateFormat = "EEE HH:mm";
									customDateFormat = new SimpleDateFormat(dateFormat);
									break;
					
		        	case SIXTY:		tickUnit = new DateTickUnit(DateTickUnit.DAY, 1);
									dateFormat = "EEE, d MMM 'yy";
									customDateFormat = new SimpleDateFormat(dateFormat);
									break;
		        	}
		        	}

		        // for intraday
		        if(stockData.getTimeFrame()==StockChartData.TimeFrame.INTRADAY) {
		        	 SegmentedTimeline fifteenTimeline = SegmentedTimeline.newFifteenMinuteTimeline(); /* This sets the timeline as a Mon-Fri 9am-4pm timeframe*/
		        	 domainAxis.setTimeline(fifteenTimeline);
		        }
		        else {        
		        // for all others
		        	SegmentedTimeline weekdayTimeline = SegmentedTimeline.newMondayThroughFridayTimeline();
		        	domainAxis.setTimeline(weekdayTimeline);
		        }
		       
		        rangeAxis.setAutoRangeIncludesZero(false);
		        // set custom tick unit
		        domainAxis.setTickUnit(tickUnit);
		        // set custom date format
		        domainAxis.setDateFormatOverride(customDateFormat);
		        // set Range for prices
		        double lowestLow = getLowestLow(dataSet);
		        double highestHigh = getHighestHigh(dataSet);
		        chart2.getXYPlot().getRangeAxis().setRange(lowestLow*0.95, highestHigh*1.05);
		        plot2.setOrientation(PlotOrientation.VERTICAL);
		        chart2.setAntiAlias(false);
		        
		       
		        //Now create the chart and chart panel
		        plot2.setRenderer(renderer);
		        
		        chartPanel2 = new ChartPanel(chart2);
		        chartPanel2.setPreferredSize(new Dimension(600, 300));
		        chartPanel2.setMouseZoomable(true);
		        chartPanel2.setMouseWheelEnabled(true);
		        
		        // Mouse listener
		        chartPanel2.addChartMouseListener(this);
		        CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
		        xCrosshair2 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
		        xCrosshair2.setLabelVisible(true);
		        yCrosshair2 = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
		        yCrosshair2.setLabelVisible(true);
		        crosshairOverlay.addDomainCrosshair(xCrosshair2);
		        crosshairOverlay.addRangeCrosshair(yCrosshair2);
		        chartPanel2.addOverlay(crosshairOverlay);
		        chartPanel2.setFillZoomRectangle(true);
		        contentPane.add(chartPanel2, BorderLayout.CENTER);
		        contentPane.validate();
		        chartPanel2.setHorizontalAxisTrace(true);
                chartPanel2.setVerticalAxisTrace(true);
                chartPanel2.repaint();
				}
				catch(Exception e) {
					System.out.println(e);
				}
			}
			

	// The following functions will display an axis of the price
	
			@Override
			public void chartMouseClicked(ChartMouseEvent event) {
			      // ignore
			}
				
			@Override
			 public void chartMouseMoved(ChartMouseEvent event) {
				Rectangle2D dataArea = this.chartPanel2.getScreenDataArea();
			    chart2 = event.getChart();
			    plot2 = (XYPlot) chart2.getPlot();
			    ValueAxis xAxis = plot2.getDomainAxis();
			    double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, RectangleEdge.BOTTOM);
			    double y = DatasetUtilities.findYValue(plot2.getDataset(), 0, x);
			    this.xCrosshair2.setValue(x);
			    this.yCrosshair2.setValue(y);
			 }
				
				
			// These functions will get the highest/lowest values of the prices
				
			protected double getLowestLow(DefaultOHLCDataset dataset){
			    double lowest;
			    lowest = dataset.getLowValue(0,0);
			    for(int i=1;i<dataset.getItemCount(0);i++){
			        if(dataset.getLowValue(0,i) < lowest){
			            lowest = dataset.getLowValue(0,i);
			        }
			    }
			    return lowest;
			}
				
			protected double getHighestHigh(DefaultOHLCDataset dataset){
				double highest;
				highest = dataset.getHighValue(0,0);
				for(int i=1;i<dataset.getItemCount(0);i++){
					if(dataset.getLowValue(0,i) > highest){
						highest = dataset.getHighValue(0,i);
				    }
				}
				return highest;
			}
			
				private void clearPlot() {
					Component[] components = contentPane.getComponents();
					for (Component component : components) {
						  if(component instanceof ChartPanel){
						        //Remove the chart Panel
						        contentPane.remove(component);
						  }
						  contentPane.repaint();
						  contentPane.revalidate();
					}
				}

}
	

