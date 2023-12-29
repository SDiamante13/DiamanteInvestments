package tech.pathtoprogramming.diamanteinvestments;

import java.awt.*;

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
import org.jfree.ui.RectangleEdge;

import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class CandleStick extends JFrame implements ChartMouseListener {

    public static final String INTRADAY_CHARTS_UNAVAILABLE_ERROR = "Error in retrieving chart: INTRADAY charts are currently unavailable.";
    private final JPanel contentPane;

    private JPanel timeSeriesSelectionPanel;
    private ChartPanel chartPanel2;
    private JFreeChart chart2;
    private XYPlot plot2;
    private Crosshair xCrosshair2;
    private Crosshair yCrosshair2;
    private final ButtonGroup timeSeriesGroup;
    private final JRadioButton rdbtn5Min;
    private final JRadioButton rdbtn15Min;
    private final JRadioButton rdbtn30Min;
    private final JRadioButton rdbtn60Min;
    private final JRadioButton rdbtnDaily;
    private final JRadioButton rdbtnWeekly;
    private final JRadioButton rdbtnMonthly;

    public CandleStick(String symbolName) {
        super();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setExtendedState(this.getExtendedState() | MAXIMIZED_BOTH);
        setBounds(100, 100, 755, 535);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        timeSeriesSelectionPanel = new JPanel();
        contentPane.add(timeSeriesSelectionPanel, BorderLayout.SOUTH);
        timeSeriesSelectionPanel.setLayout(new GridLayout(4, 2, 10, 10));

        rdbtn5Min = new JRadioButton("5 Minute");
        rdbtn5Min.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(StockChartData.TimeFrame.INTRADAY, symbolName, StockChartData.Interval.FIVE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, INTRADAY_CHARTS_UNAVAILABLE_ERROR);
            }
        });
        timeSeriesSelectionPanel.add(rdbtn5Min);

        rdbtn30Min = new JRadioButton("30 Minute");
        rdbtn30Min.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(StockChartData.TimeFrame.INTRADAY, symbolName, StockChartData.Interval.THIRTY);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, INTRADAY_CHARTS_UNAVAILABLE_ERROR);
            }
        });
        timeSeriesSelectionPanel.add(rdbtn30Min);

        rdbtn15Min = new JRadioButton("15 Minute");
        rdbtn15Min.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(StockChartData.TimeFrame.INTRADAY, symbolName, StockChartData.Interval.FIFTEEN);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, INTRADAY_CHARTS_UNAVAILABLE_ERROR);
            }
        });
        timeSeriesSelectionPanel.add(rdbtn15Min);

        rdbtn60Min = new JRadioButton("60 Minute");
        rdbtn60Min.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(StockChartData.TimeFrame.INTRADAY, symbolName, StockChartData.Interval.SIXTY);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, INTRADAY_CHARTS_UNAVAILABLE_ERROR);
            }
        });
        timeSeriesSelectionPanel.add(rdbtn60Min);

        rdbtnDaily = new JRadioButton("Daily");
        rdbtnDaily.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(StockChartData.TimeFrame.DAILY, symbolName, StockChartData.Interval.FIVE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An error occured in displaying the daily chart.");
            }
        });
        timeSeriesSelectionPanel.add(rdbtnDaily);

        rdbtnWeekly = new JRadioButton("Weekly");
        rdbtnWeekly.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(StockChartData.TimeFrame.WEEKLY, symbolName, StockChartData.Interval.FIVE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An error occurred in displaying the weekly chart.");
            }
        });
        timeSeriesSelectionPanel.add(rdbtnWeekly);

        rdbtnMonthly = new JRadioButton("Monthly");
        rdbtnMonthly.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(StockChartData.TimeFrame.MONTHLY, symbolName, StockChartData.Interval.FIVE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An error occurred in displaying the monthly chart.");
            }
        });
        timeSeriesSelectionPanel.add(rdbtnMonthly);

        timeSeriesGroup = new ButtonGroup();
        timeSeriesGroup.add(rdbtn5Min);
        timeSeriesGroup.add(rdbtn15Min);
        timeSeriesGroup.add(rdbtn30Min);
        timeSeriesGroup.add(rdbtn60Min);
        timeSeriesGroup.add(rdbtnDaily);
        timeSeriesGroup.add(rdbtnWeekly);
        timeSeriesGroup.add(rdbtnMonthly);

        rdbtnDaily.setSelected(true);
    }

    public JPanel getContentPane() {
        return contentPane;
    }

    public void createContent(StockChartData.TimeFrame timeF, String symbol, StockChartData.Interval interval) {
        try {
            /*Grab the stock data from the Alpha Vantage API*/
            StockChartData stockData = new StockChartData(timeF, symbol, interval);
            final int size = stockData.getOpens().size();
            OHLCDataItem[] data = new OHLCDataItem[size];
            for (Date date : stockData.getDates()) {
                date.setYear(date.getYear() - 1900); // edit years
                date.setMonth(date.getMonth() - 1);
            }
            for (int i = 0; i < size; i++) {
                data[i] = new OHLCDataItem(stockData.getDates().pop(), stockData.getOpens().pop(), stockData.getHighs().pop(), stockData.getLows().pop(), stockData.getCloses().pop(), stockData.getVolumes().pop());
            }

            CandlestickRenderer renderer = new CandlestickRenderer();
            DefaultOHLCDataset dataSet = new DefaultOHLCDataset(1, data);
            chart2 = ChartFactory.createCandlestickChart(symbol, "Date", "Price", dataSet, false);
            plot2 = chart2.getXYPlot();
            DateAxis domainAxis = (DateAxis) plot2.getDomainAxis();
            NumberAxis rangeAxis = new NumberAxis();
            renderer.setSeriesPaint(0, Color.BLACK);
            renderer.setDrawVolume(true);
            DateTickUnit tickUnit = new DateTickUnit(DateTickUnit.DAY, 1);
            SimpleDateFormat customDateFormat = new SimpleDateFormat("MMM");
            // Set dateFormat depending on TimeSeries
            String dateFormat = "MMM-dd";
            if (stockData.getTimeFrame() == StockChartData.TimeFrame.MONTHLY) {
                tickUnit = new DateTickUnit(DateTickUnit.MONTH, 9);
                dateFormat = "yyyy-MMM";
                customDateFormat = new SimpleDateFormat(dateFormat);
            } else if (stockData.getTimeFrame() == StockChartData.TimeFrame.WEEKLY) {
                tickUnit = new DateTickUnit(DateTickUnit.MONTH, 8);
                dateFormat = "MMM-YYYY";
                customDateFormat = new SimpleDateFormat(dateFormat);
            } else if (stockData.getTimeFrame() == StockChartData.TimeFrame.DAILY) {
                tickUnit = new DateTickUnit(DateTickUnit.DAY, 20);
                dateFormat = "MMM-dd";
                customDateFormat = new SimpleDateFormat(dateFormat);

            } else if (stockData.getTimeFrame() == StockChartData.TimeFrame.INTRADAY) { // set Intraday custom dateformats and tick units
                switch (stockData.getInterval()) {
                    case ONE:
                        tickUnit = new DateTickUnit(DateTickUnit.MINUTE, 10);
                        dateFormat = "HH:mm:ss";
                        customDateFormat = new SimpleDateFormat(dateFormat);
                        break;

                    case FIVE:
                        tickUnit = new DateTickUnit(DateTickUnit.MINUTE, 30);
                        dateFormat = "HH:mm";
                        customDateFormat = new SimpleDateFormat(dateFormat);
                        break;

                    case FIFTEEN:
                        tickUnit = new DateTickUnit(DateTickUnit.HOUR, 2);
                        dateFormat = "EEE, HH:mm";
                        customDateFormat = new SimpleDateFormat(dateFormat);
                        break;

                    case THIRTY:
                        tickUnit = new DateTickUnit(DateTickUnit.HOUR, 6);
                        dateFormat = "EEE HH:mm";
                        customDateFormat = new SimpleDateFormat(dateFormat);
                        break;

                    case SIXTY:
                        tickUnit = new DateTickUnit(DateTickUnit.DAY, 1);
                        dateFormat = "EEE, d MMM 'yy";
                        customDateFormat = new SimpleDateFormat(dateFormat);
                        break;
                }
            }

            // for intraday
            if (stockData.getTimeFrame() == StockChartData.TimeFrame.INTRADAY) {
                SegmentedTimeline fifteenTimeline = SegmentedTimeline.newFifteenMinuteTimeline(); /* This sets the timeline as a Mon-Fri 9am-4pm timeframe*/
                domainAxis.setTimeline(fifteenTimeline);
            } else {
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
            chart2.getXYPlot().getRangeAxis().setRange(lowestLow * 0.95, highestHigh * 1.05);
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
        } catch (Exception e) {
            System.out.println(e);
        }
    }

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

    protected double getLowestLow(DefaultOHLCDataset dataset) {
        double lowest;
        lowest = dataset.getLowValue(0, 0);
        for (int i = 1; i < dataset.getItemCount(0); i++) {
            if (dataset.getLowValue(0, i) < lowest) {
                lowest = dataset.getLowValue(0, i);
            }
        }
        return lowest;
    }

    protected double getHighestHigh(DefaultOHLCDataset dataset) {
        double highest;
        highest = dataset.getHighValue(0, 0);
        for (int i = 1; i < dataset.getItemCount(0); i++) {
            if (dataset.getLowValue(0, i) > highest) {
                highest = dataset.getHighValue(0, i);
            }
        }
        return highest;
    }

    private void clearPlot() {
        Component[] components = contentPane.getComponents();
        for (Component component : components) {
            if (component instanceof ChartPanel) {
                //Remove the chart Panel
                contentPane.remove(component);
            }
            contentPane.repaint();
            contentPane.revalidate();
        }
    }
}
