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
import java.util.function.Supplier;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class CandleStick extends JFrame implements ChartMouseListener {

    public static final String INTRADAY_CHARTS_UNAVAILABLE_ERROR = "Error in retrieving chart: INTRADAY charts are currently unavailable.";
    private final JPanel contentPane;

    private ChartPanel chartPanel2;
    private JFreeChart chart2;
    private XYPlot plot2;
    private Crosshair xCrosshair2;
    private Crosshair yCrosshair2;

    public CandleStick(String symbolName, Supplier<StockChartData> stockChartDataSupplier) {
        super();
        contentPane = new JPanel();

        applesauce(symbolName, stockChartDataSupplier);
    }

    private void applesauce(String symbolName, Supplier<StockChartData> stockChartDataSupplier) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setExtendedState(this.getExtendedState() | MAXIMIZED_BOTH);
        setBounds(100, 100, 755, 535);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel timeSeriesSelectionPanel = new JPanel();
        contentPane.add(timeSeriesSelectionPanel, BorderLayout.SOUTH);
        timeSeriesSelectionPanel.setLayout(new GridLayout(4, 2, 10, 10));

        JRadioButton rdbtn5Min = new JRadioButton("5 Minute");
        rdbtn5Min.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(symbolName, () -> new StockChartData(TimeFrame.INTRADAY, symbolName, Interval.FIVE));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, INTRADAY_CHARTS_UNAVAILABLE_ERROR);
            }
        });
        timeSeriesSelectionPanel.add(rdbtn5Min);

        JRadioButton rdbtn30Min = new JRadioButton("30 Minute");
        rdbtn30Min.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(symbolName,
                        () -> new StockChartData(TimeFrame.INTRADAY, symbolName, Interval.THIRTY));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, INTRADAY_CHARTS_UNAVAILABLE_ERROR);
            }
        });
        timeSeriesSelectionPanel.add(rdbtn30Min);

        JRadioButton rdbtn15Min = new JRadioButton("15 Minute");
        rdbtn15Min.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(symbolName, () -> new StockChartData(TimeFrame.INTRADAY, symbolName, Interval.FIFTEEN));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, INTRADAY_CHARTS_UNAVAILABLE_ERROR);
            }
        });
        timeSeriesSelectionPanel.add(rdbtn15Min);

        JRadioButton rdbtn60Min = new JRadioButton("60 Minute");
        rdbtn60Min.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(symbolName, () -> new StockChartData(TimeFrame.INTRADAY, symbolName, Interval.SIXTY));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, INTRADAY_CHARTS_UNAVAILABLE_ERROR);
            }
        });
        timeSeriesSelectionPanel.add(rdbtn60Min);

        JRadioButton rdbtnDaily = new JRadioButton("Daily");
        rdbtnDaily.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(symbolName, () -> new StockChartData(TimeFrame.DAILY, symbolName, Interval.FIVE));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An error occured in displaying the daily chart.");
            }
        });
        timeSeriesSelectionPanel.add(rdbtnDaily);

        JRadioButton rdbtnWeekly = new JRadioButton("Weekly");
        rdbtnWeekly.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(symbolName, () -> new StockChartData(TimeFrame.WEEKLY, symbolName, Interval.FIVE));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An error occurred in displaying the weekly chart.");
            }
        });
        timeSeriesSelectionPanel.add(rdbtnWeekly);

        JRadioButton rdbtnMonthly = new JRadioButton("Monthly");
        rdbtnMonthly.addItemListener(arg0 -> {
            try {
                clearPlot();
                createContent(symbolName, () -> new StockChartData(TimeFrame.MONTHLY, symbolName, Interval.FIVE));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An error occurred in displaying the monthly chart.");
            }
        });
        timeSeriesSelectionPanel.add(rdbtnMonthly);

        ButtonGroup timeSeriesGroup = new ButtonGroup();
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

    private void createContent(String symbol, Supplier<StockChartData> stockChartDataSupplier) {
        /*Grab the stock data from the Alpha Vantage API*/
        StockChartData stockData = stockChartDataSupplier.get();
        OHLCDataItem[] data = createDataItems(stockData);

        CandlestickRenderer renderer = new CandlestickRenderer();
        DefaultOHLCDataset dataSet = new DefaultOHLCDataset(1, data);
        chart2 = ChartFactory.createCandlestickChart(
                symbol,
                "Date",
                "Price",
                dataSet,
                false);
        plot2 = chart2.getXYPlot();
        DateAxis domainAxis = (DateAxis) plot2.getDomainAxis();
        NumberAxis rangeAxis = new NumberAxis();
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setDrawVolume(true);

        ChartOptions chartOptions = determineChartOptions(stockData);

        setTimeline(stockData, domainAxis);

        setAttributesOnAxis(domainAxis, rangeAxis, chartOptions.getTickUnit(), chartOptions.getCustomDateFormat());
        setPriceRangesOnXYPlot(getLowestLow(dataSet), getHighestHigh(dataSet));

        createChart(renderer);
    }

    private OHLCDataItem[] createDataItems(StockChartData stockData) {
        final int size = stockData.getOpens().size();
        OHLCDataItem[] data = new OHLCDataItem[size];
        for (Date date : stockData.getDates()) {
            date.setYear(date.getYear() - 1900); // edit years
            date.setMonth(date.getMonth() - 1);
        }
        for (int i = 0; i < size; i++) {
            data[i] = new OHLCDataItem(
                    stockData.getDates().pop(),
                    stockData.getOpens().pop(),
                    stockData.getHighs().pop(),
                    stockData.getLows().pop(),
                    stockData.getCloses().pop(),
                    stockData.getVolumes().pop());
        }
        return data;
    }

    private class ChartOptions {

        private DateTickUnit tickUnit;
        private SimpleDateFormat customDateFormat = new SimpleDateFormat("MMM");
        private String dateFormat;

        public ChartOptions(DateTickUnit tickUnit, String dateFormat) {
            this.tickUnit = tickUnit;
            this.dateFormat = dateFormat;
        }

        public DateTickUnit getTickUnit() {
            return tickUnit;
        }

        public SimpleDateFormat getCustomDateFormat() {
            return customDateFormat;
        }
    }

    // TODO: refactor to polymorphism
    private ChartOptions determineChartOptions(StockChartData stockData) {
        if (stockData.getTimeFrame() == TimeFrame.MONTHLY) {
            return new ChartOptions(
                    new DateTickUnit(DateTickUnit.MONTH, 9),
                    "yyyy-MMM");
        } else if (stockData.getTimeFrame() == TimeFrame.WEEKLY) {
            return new ChartOptions(
                    new DateTickUnit(DateTickUnit.MONTH, 8),
                    "MMM-YYYY");
        } else if (stockData.getTimeFrame() == TimeFrame.DAILY) {
            return new ChartOptions(
                    new DateTickUnit(DateTickUnit.DAY, 20),
                    "MMM-dd");
        } else if (stockData.getTimeFrame() == TimeFrame.INTRADAY) {
            // set Intraday custom dateformats and tick units

            // FIXME: Possible bug with the date format
            switch (stockData.getInterval()) {
                case ONE:
                    return new ChartOptions(
                            new DateTickUnit(DateTickUnit.MINUTE, 10),
                            "HH:mm:ss");
                case FIVE:
                    return new ChartOptions(
                            new DateTickUnit(DateTickUnit.MINUTE, 30),
                            "HH:mm");
                case FIFTEEN:
                    return new ChartOptions(
                            new DateTickUnit(DateTickUnit.HOUR, 2),
                            "EEE, HH:mm");
                case THIRTY:
                    return new ChartOptions(
                            new DateTickUnit(DateTickUnit.HOUR, 6),
                            "EEE HH:mm");
                case SIXTY:
                    return new ChartOptions(
                            new DateTickUnit(DateTickUnit.DAY, 1),
                            "EEE, d MMM 'yy");
            }
        }

        return new ChartOptions(
                new DateTickUnit(DateTickUnit.DAY, 1),
                "MMM-dd");
    }

    private void setTimeline(StockChartData stockData, DateAxis domainAxis) {
        // for intraday
        if (stockData.getTimeFrame() == TimeFrame.INTRADAY) {
            SegmentedTimeline fifteenTimeline = SegmentedTimeline.newFifteenMinuteTimeline(); /* This sets the timeline as a Mon-Fri 9am-4pm timeframe*/
            domainAxis.setTimeline(fifteenTimeline);
        } else {
            // for all others
            SegmentedTimeline weekdayTimeline = SegmentedTimeline.newMondayThroughFridayTimeline();
            domainAxis.setTimeline(weekdayTimeline);
        }
    }

    private void setAttributesOnAxis(DateAxis domainAxis,
                                     NumberAxis rangeAxis,
                                     DateTickUnit tickUnit,
                                     SimpleDateFormat customDateFormat) {
        rangeAxis.setAutoRangeIncludesZero(false);
        domainAxis.setTickUnit(tickUnit);
        domainAxis.setDateFormatOverride(customDateFormat);
    }

    private void setPriceRangesOnXYPlot(double lowestLow, double highestHigh) {
        chart2.getXYPlot().getRangeAxis()
                .setRange(lowestLow * 0.95, highestHigh * 1.05);
        plot2.setOrientation(PlotOrientation.VERTICAL);
        chart2.setAntiAlias(false);
    }

    private void createChart(CandlestickRenderer renderer) {
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
