package tech.pathtoprogramming.diamanteinvestments;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.ui.RectangleEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StockForm extends JFrame implements ChartMouseListener {

    private JPanel contentPane;
    private JTextField txtSearch;
    private JLabel lblWatchList;
    private JButton btnSearchStock;
    private JTable stockTable;
    private JScrollPane scrollPane;
    private JButton btnUpdate;
    private String usernameTable;

    Logger log = LoggerFactory.getLogger(StockForm.class);

    // Declaring this early so the Panel can be invisible if the user's watchlist is empty
    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    DefaultTableModel model = new DefaultTableModel();
    ArrayList<String> symbolList = new ArrayList<>();

    // Chart items
    private JPanel chartTab;
    private ChartPanel chartPanel;
    private JFreeChart chart;
    private XYPlot plot;
    private Crosshair xCrosshair;
    private Crosshair yCrosshair;


    // Summary Pane: Labels
    JLabel lblStockSymbol = new JLabel("SYMB");
    JLabel lblChange = new JLabel("+0.00");
    JLabel lblChangePercent = new JLabel("0.00%");
    JLabel lblStockName = new JLabel("Stock Name");
    JLabel lblOpenSt = new JLabel("Open");
    JLabel lblCloseSt = new JLabel("Close");
    JLabel lblDaysRangeSt = new JLabel("Open");
    JLabel lblWeekRangeSt = new JLabel("Open");
    JLabel lblVolumeSt = new JLabel("Open");
    JLabel lblAvgVolumeSt = new JLabel("Open");
    JLabel lblMarketCapSt = new JLabel("Market Cap");
    JLabel lblPeRatioSt = new JLabel("P/E Ratio");
    JLabel lblEpsSt = new JLabel("EPS");
    JLabel lblFloatShortedSt = new JLabel("Float Shorted");
    JLabel lblDayMovingAverageSt = new JLabel("50 Day");
    JLabel lblDayMovingSt = new JLabel("100 Day");
    JLabel lblStockPic = new JLabel("");

    Connection connection = null;

    public StockForm(Connection connection, String username) {
        initialize(connection, username);
    }

    private void initialize(Connection connection, String username) {
        usernameTable = username;
        this.connection = connection;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(this.getExtendedState() | this.MAXIMIZED_BOTH);
        setBounds(100, 100, 1445, 763);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblSearch = new JLabel("Search");
        lblSearch.setFont(new Font("Calibri", Font.BOLD, 18));
        lblSearch.setBounds(520, 238, 51, 23);
        contentPane.add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setBounds(585, 238, 166, 23);
        contentPane.add(txtSearch);
        txtSearch.setColumns(10);

        lblWatchList = new JLabel("Watch List");
        lblWatchList.setFont(new Font("Calibri", Font.BOLD, 18));
        lblWatchList.setBounds(171, 251, 78, 23);
        contentPane.add(lblWatchList);

        // Stock Summary Panel (Search Button)
        btnSearchStock = new JButton("Search Stock");
        btnSearchStock.setName("btnSearchStock");
        btnSearchStock.setFont(new Font("Calibri", Font.BOLD, 14));
        btnSearchStock.setBounds(585, 272, 166, 23);
        contentPane.add(btnSearchStock);
        btnSearchStock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                writeData(txtSearch.getText());
            }
        });

        scrollPane = new JScrollPane();
        scrollPane.setBounds(32, 306, 423, 300);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        Object[] columnHeaders = {"Stock Symbol", "Price", "+/- Change", "% Change"};
        stockTable = new JTable();
        model.setColumnIdentifiers(columnHeaders);
        stockTable.setModel(model);
        stockTable.setBackground(Color.black);
        stockTable.setForeground(Color.white);
        stockTable.setFont(new Font("Calibri", Font.BOLD, 14));
        stockTable.setRowHeight(30);
        scrollPane.setViewportView(stockTable);

        ListSelectionModel listModel = stockTable.getSelectionModel();
        listModel.addListSelectionListener(e -> {
            if (!listModel.isSelectionEmpty()) {
                // get selected row
                int selectedRow = listModel.getMinSelectionIndex();
                String stockSelection = model.getValueAt(selectedRow, 0).toString();
                if (stockSelection != lblStockSymbol.getText().toUpperCase()) {
                    // if the selected stock is not already on the summary pane then write Data
                    writeData(stockSelection);
                }
                // display chart
                clearPlot();
                createContent(stockSelection);
                contentPane.repaint();
                contentPane.revalidate();
            }
        });

        // Declared as global
        tabbedPane.setBounds(815, 238, 500, 385);
        contentPane.add(tabbedPane);

        // grab the Database stock values
        this.loadTable(); // loads watchlist with stocks and values from the user's unique table
        if (!symbolList.isEmpty()) {
            this.writeData(symbolList.get(0)); // For default the summary pane will have your first watchlist symbol
        } else {
            tabbedPane.setVisible(false);
        }

        while (!symbolList.isEmpty()) {
            this.retrieveStockStats(symbolList.get(0));
        }
        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(150, 625, 159, 33);
        contentPane.add(btnUpdate);

        btnUpdate.addActionListener(arg0 -> {
            try {
                // reload table
                clearTable();
                loadTable(); // fill array list
                while (!symbolList.isEmpty()) {
                    retrieveStockStats(symbolList.get(0));
                }
            } catch (Exception e) {

            }
        });

        JLabel lblStockIcon = new JLabel("");
        Image imageStock = new ImageIcon(this.getClass().getResource("/stockmarket-icon.png")).getImage();
        lblStockIcon.setIcon(new ImageIcon(imageStock));
        lblStockIcon.setBounds(50, 40, 118, 118);
        contentPane.add(lblStockIcon);


        JPanel summaryPanel = new JPanel();
        tabbedPane.addTab("Summary", null, summaryPanel, null);
        summaryPanel.setLayout(null);
        lblStockSymbol.setVerticalAlignment(SwingConstants.TOP);

        lblStockSymbol.setFont(new Font("Calibri", Font.BOLD, 18));
        lblStockSymbol.setBounds(20, 51, 100, 20);
        summaryPanel.add(lblStockSymbol);
        lblChange.setHorizontalAlignment(SwingConstants.RIGHT);

        lblChange.setFont(new Font("Calibri", Font.BOLD, 12));
        lblChange.setBounds(0, 70, 40, 20);
        summaryPanel.add(lblChange);

        lblChangePercent.setFont(new Font("Calibri", Font.BOLD, 12));
        lblChangePercent.setBounds(52, 70, 50, 20);
        summaryPanel.add(lblChangePercent);
        lblStockName.setHorizontalAlignment(SwingConstants.CENTER);
        lblStockName.setVerticalAlignment(SwingConstants.TOP);

        lblStockName.setFont(new Font("Calibri", Font.BOLD, 20));
        lblStockName.setBounds(112, 10, 288, 79);
        summaryPanel.add(lblStockName);

        JSeparator separator = new JSeparator();
        separator.setBounds(0, 90, 500, 2);
        summaryPanel.add(separator);

        JLabel lblOpen = new JLabel("Open");
        lblOpen.setFont(new Font("Calibri", Font.BOLD, 12));
        lblOpen.setBounds(20, 120, 46, 14);
        summaryPanel.add(lblOpen);

        JLabel lblClose = new JLabel("Close");
        lblClose.setFont(new Font("Calibri", Font.BOLD, 12));
        lblClose.setBounds(20, 160, 46, 14);
        summaryPanel.add(lblClose);

        JLabel lblDaysRange = new JLabel("Day's Range");
        lblDaysRange.setFont(new Font("Calibri", Font.BOLD, 12));
        lblDaysRange.setBounds(20, 200, 76, 13);
        summaryPanel.add(lblDaysRange);

        JLabel lblWeekRange = new JLabel("52 Week Range");
        lblWeekRange.setFont(new Font("Calibri", Font.BOLD, 12));
        lblWeekRange.setBounds(20, 240, 87, 13);
        summaryPanel.add(lblWeekRange);

        JLabel lblVolume = new JLabel("Volume");
        lblVolume.setFont(new Font("Calibri", Font.BOLD, 12));
        lblVolume.setBounds(20, 280, 46, 14);
        summaryPanel.add(lblVolume);

        JLabel lblAverageVolume = new JLabel("Average Volume");
        lblAverageVolume.setFont(new Font("Calibri", Font.BOLD, 12));
        lblAverageVolume.setBounds(20, 320, 100, 13);
        summaryPanel.add(lblAverageVolume);

        lblOpenSt.setFont(new Font("Calibri", Font.BOLD, 12));
        lblOpenSt.setBounds(125, 120, 76, 14);
        summaryPanel.add(lblOpenSt);

        lblCloseSt.setFont(new Font("Calibri", Font.BOLD, 12));
        lblCloseSt.setBounds(125, 160, 76, 14);
        summaryPanel.add(lblCloseSt);

        lblDaysRangeSt.setFont(new Font("Calibri", Font.BOLD, 12));
        lblDaysRangeSt.setBounds(125, 200, 111, 15);
        summaryPanel.add(lblDaysRangeSt);

        lblWeekRangeSt.setFont(new Font("Calibri", Font.BOLD, 12));
        lblWeekRangeSt.setBounds(125, 240, 111, 15);
        summaryPanel.add(lblWeekRangeSt);

        lblVolumeSt.setFont(new Font("Calibri", Font.BOLD, 12));
        lblVolumeSt.setBounds(125, 280, 76, 14);
        summaryPanel.add(lblVolumeSt);

        lblAvgVolumeSt.setFont(new Font("Calibri", Font.BOLD, 12));
        lblAvgVolumeSt.setBounds(125, 320, 76, 14);
        summaryPanel.add(lblAvgVolumeSt);

        JSeparator separator_1 = new JSeparator();
        separator_1.setOrientation(SwingConstants.VERTICAL);
        separator_1.setBounds(112, 90, 1, 270);
        summaryPanel.add(separator_1);

        JLabel lblMarketCap = new JLabel("Market Cap");
        lblMarketCap.setFont(new Font("Calibri", Font.BOLD, 12));
        lblMarketCap.setBounds(260, 120, 66, 15);
        summaryPanel.add(lblMarketCap);

        JLabel lblPeRatio = new JLabel("P/E Ratio");
        lblPeRatio.setFont(new Font("Calibri", Font.BOLD, 12));
        lblPeRatio.setBounds(260, 160, 46, 14);
        summaryPanel.add(lblPeRatio);

        JLabel lblEps = new JLabel("EPS");
        lblEps.setFont(new Font("Calibri", Font.BOLD, 12));
        lblEps.setBounds(260, 200, 46, 14);
        summaryPanel.add(lblEps);

        JLabel lblFloatShorted = new JLabel("Float Shorted");
        lblFloatShorted.setFont(new Font("Calibri", Font.BOLD, 12));
        lblFloatShorted.setBounds(260, 240, 66, 15);
        summaryPanel.add(lblFloatShorted);

        JLabel lblDayMovingAverage = new JLabel("50-Day Moving Average");
        lblDayMovingAverage.setFont(new Font("Calibri", Font.BOLD, 12));
        lblDayMovingAverage.setBounds(245, 280, 120, 15);
        summaryPanel.add(lblDayMovingAverage);

        JLabel lblDayMoving = new JLabel("100-Day Moving Average");
        lblDayMoving.setFont(new Font("Calibri", Font.BOLD, 12));
        lblDayMoving.setBounds(245, 320, 126, 15);
        summaryPanel.add(lblDayMoving);

        JSeparator separator_2 = new JSeparator();
        separator_2.setOrientation(SwingConstants.VERTICAL);
        separator_2.setBounds(235, 90, 1, 270);
        summaryPanel.add(separator_2);

        JSeparator separator_3 = new JSeparator();
        separator_3.setOrientation(SwingConstants.VERTICAL);
        separator_3.setBounds(377, 90, 1, 270);
        summaryPanel.add(separator_3);

        lblMarketCapSt.setFont(new Font("Calibri", Font.BOLD, 12));
        lblMarketCapSt.setBounds(390, 120, 70, 14);
        summaryPanel.add(lblMarketCapSt);

        lblPeRatioSt.setFont(new Font("Calibri", Font.BOLD, 12));
        lblPeRatioSt.setBounds(390, 160, 70, 14);
        summaryPanel.add(lblPeRatioSt);

        lblEpsSt.setFont(new Font("Calibri", Font.BOLD, 12));
        lblEpsSt.setBounds(390, 200, 70, 14);
        summaryPanel.add(lblEpsSt);

        lblFloatShortedSt.setFont(new Font("Calibri", Font.BOLD, 12));
        lblFloatShortedSt.setBounds(390, 240, 70, 14);
        summaryPanel.add(lblFloatShortedSt);

        lblDayMovingAverageSt.setFont(new Font("Calibri", Font.BOLD, 12));
        lblDayMovingAverageSt.setBounds(390, 280, 70, 14);
        summaryPanel.add(lblDayMovingAverageSt);

        lblDayMovingSt.setFont(new Font("Calibri", Font.BOLD, 12));
        lblDayMovingSt.setBounds(390, 320, 70, 14);
        summaryPanel.add(lblDayMovingSt);

        JButton btnAddStock = new JButton("");
        btnAddStock.addActionListener(arg0 -> {
            // add stock symbol lblStockSymbol to UsernameWatchList
            try {
                String query = "insert into " + usernameTable + "WatchList (symbol) values (?)";
                PreparedStatement pst = connection.prepareStatement(query);
                pst.setString(1, lblStockSymbol.getText());
                pst.execute();
                JOptionPane.showMessageDialog(null, "Stock added to your watchlist");
                pst.close();
                // reload table
                clearTable();
                loadTable(); // fill array list
                while (!symbolList.isEmpty()) {
                    retrieveStockStats(symbolList.get(0));
                }
            } catch (Exception e) {
                log.error("Error occurred: ", e);
            }


        });
        Image imageAdd = new ImageIcon(this.getClass().getResource("/add-icon.png")).getImage();
        btnAddStock.setIcon(new ImageIcon(imageAdd));
        btnAddStock.setBounds(442, 15, 33, 33);
        summaryPanel.add(btnAddStock);

        JButton btnRemoveStock = new JButton("");
        btnRemoveStock.addActionListener(arg0 -> {
            // remove stock symbol lblStockSymbol from UsernameWatchList
            try {
                String query = "delete from " + usernameTable + "WatchList where symbol= '" + lblStockSymbol.getText() + "'  ";
                PreparedStatement pst = connection.prepareStatement(query);
                pst.execute();
                JOptionPane.showMessageDialog(null, "Stock removed from your watchlist");
                pst.close();

                // reload table
                clearTable();
                loadTable(); // fill array list
                while (!symbolList.isEmpty()) {
                    retrieveStockStats(symbolList.get(0));
                }
            } catch (Exception e) {
                log.error("Error occurred: ", e);
            }
        });
        Image imageRemove = new ImageIcon(this.getClass().getResource("/remove-icon.png")).getImage();
        btnRemoveStock.setIcon(new ImageIcon(imageRemove));
        btnRemoveStock.setBounds(442, 46, 33, 33);
        summaryPanel.add(btnRemoveStock);

        lblStockPic.setBounds(15, 10, 100, 40);
        summaryPanel.add(lblStockPic);

        chartTab = new JPanel();
        tabbedPane.addTab("Chart", null, chartTab, null);
        chartTab.setLayout(new BorderLayout());

        // The chart is displayed when the tab is changed to the chart panel
        tabbedPane.addChangeListener(e -> createContent(lblStockSymbol.getText()));

        JPanel SelectionPanel = new JPanel();
        chartTab.add(SelectionPanel, BorderLayout.SOUTH);
        SelectionPanel.setLayout(new GridLayout(0, 1, 0, 10));

        JButton btnDetach = new JButton("Open in new window");
        btnDetach.addActionListener(arg0 -> {
            CandleStick cs = new CandleStick(lblStockSymbol.getText());
            cs.setTitle("Diamante Investments - Candlestick Chart");
            cs.setVisible(true);

        });
        btnDetach.setFont(new Font("Calibri", Font.BOLD, 16));
        SelectionPanel.add(btnDetach);

        JLabel lblMainLogo1 = new JLabel("");
        Image imageMainLogo1 = new ImageIcon(this.getClass().getResource("/mainLogo1.png")).getImage();
        lblMainLogo1.setIcon(new ImageIcon(imageMainLogo1));
        lblMainLogo1.setBounds(250, 20, 600, 100);
        contentPane.add(lblMainLogo1);

        JLabel lblMainLogo2 = new JLabel("");
        Image imageMainLogo2 = new ImageIcon(this.getClass().getResource("/mainLogo2.png")).getImage();
        lblMainLogo2.setIcon(new ImageIcon(imageMainLogo2));
        lblMainLogo2.setBounds(250, 80, 600, 100);
        contentPane.add(lblMainLogo2);

        JPanel profilePanel = new JPanel();
        profilePanel.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
        profilePanel.setBounds(1107, 40, 232, 100);
        contentPane.add(profilePanel);
        profilePanel.setLayout(null);

        JLabel lblUserProfile = new JLabel("<html>" + usernameTable + "</html");
        lblUserProfile.setVerticalAlignment(SwingConstants.TOP);
        lblUserProfile.setBounds(10, 33, 190, 47);
        profilePanel.add(lblUserProfile);
        lblUserProfile.setFont(new Font("Calibri", Font.BOLD, 16));

        JLabel lblProfile = new JLabel("Username:");
        lblProfile.setBounds(10, 11, 72, 20);
        profilePanel.add(lblProfile);
        lblProfile.setFont(new Font("Calibri", Font.BOLD, 16));
    }

    public void loadTable() {
        try {
            // grab symbols from SQLite table
            // table is now username + WatchList
            String query = "select symbol from " + usernameTable + "WatchList";
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            // load symbols into an ArrayList
            while (rs.next()) {
                symbolList.add(rs.getString(1));
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
    }

    // This method will call tech.pathtoprogramming.diamanteinvestments.StockSymbol and load values to the WatchList table
    public void retrieveStockStats(String stockS) {
        StockSymbol tableSymbol = new StockSymbol(stockS, "https://www.marketwatch.com", "https://www.alphavantage.co");
        Object[] row = {symbolList.get(0), tableSymbol.getPrice(), tableSymbol.getChange(), tableSymbol.getChangePercent()};
        model.addRow(row);
        symbolList.remove(0);
    }

    public void clearTable() {
        model.setRowCount(0);
    }

    // This method will write any stock symbol's details to the Summary Pane
    public void writeData(String symbolMark) {
        try {
            StockSymbol stockSearched = new StockSymbol(symbolMark, "https://www.marketwatch.com", "https://www.alphavantage.co");
            if (stockSearched.getStockName().equals("not found")) { /* This will check if the symbol is valid */
                JOptionPane.showMessageDialog(null, "Please enter a valid stock symbol to search.");
                return;
            }
            lblStockSymbol.setText(symbolMark.toUpperCase());
            lblStockName.setText("<html>" + stockSearched.getStockName() + "</html>");
            // set colors (green = positive,  red = negative)
            if (stockSearched.getChange().charAt(0) == '-') {
                lblChange.setText(stockSearched.getChange());
                lblChange.setForeground(Color.RED);
            } else {
                lblChange.setText("+" + stockSearched.getChange());
                lblChange.setForeground(Color.GREEN.darker());
            }
            if (stockSearched.getChangePercent().charAt(0) == '-') {
                lblChangePercent.setText(stockSearched.getChangePercent() + "%");
                lblChangePercent.setForeground(Color.RED);
            } else {
                lblChangePercent.setText("+" + stockSearched.getChangePercent() + "%");
                lblChangePercent.setForeground(Color.GREEN.darker());
            }


            lblOpenSt.setText(stockSearched.getOpen());
            lblCloseSt.setText(stockSearched.getClose());
            lblDaysRangeSt.setText(stockSearched.getLow() + " - " + stockSearched.getHigh());
            lblWeekRangeSt.setText(stockSearched.getYearlyLow() + " - " + stockSearched.getYearlyHigh());
            lblVolumeSt.setText(stockSearched.getVolume());
            lblAvgVolumeSt.setText(stockSearched.getAverageVolume());

            lblMarketCapSt.setText(stockSearched.getMarketCap());
            lblPeRatioSt.setText(stockSearched.getPeRatio());
            lblEpsSt.setText(stockSearched.getEps());
            lblFloatShortedSt.setText(stockSearched.getFloatShorted());
            lblDayMovingAverageSt.setText(stockSearched.getFiftyDayMA());
            lblDayMovingSt.setText(stockSearched.getHundredDayMA());


            // set stock icon image
            if (stockSearched.getIconUrl().toString().equals("https://www.google.com/webhp")) { // if there is no image then leave the JLabel blank
                lblStockPic.setVisible(false);
            } else { // if an image exist then display it
                BufferedImage imageStockPic = ImageIO.read(stockSearched.getIconUrl());
                lblStockPic.setIcon(new ImageIcon(imageStockPic));
                lblStockPic.setVisible(true);
            }

            tabbedPane.setVisible(true);
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
    }


    // The following functions will aid in displaying the candlestick chart on to the chartPanel
    public void createContent(String symbol) {
        try {
            // Grab the stock data from the Alpha Vantage API
            StockChartData stockData = new StockChartData(StockChartData.TimeFrame.DAILY, symbol, StockChartData.Interval.FIFTEEN);
            final int size = stockData.getOpens().size();
            OHLCDataItem[] data = new OHLCDataItem[size];
            for (Date date : stockData.getDates()) {
                date.setYear(date.getYear() - 1900);
                date.setMonth(date.getMonth() - 1);
            }
            for (int i = 0; i < size; i++) {
                data[i] = new OHLCDataItem(stockData.getDates().pop(), stockData.getOpens().pop(), stockData.getHighs().pop(), stockData.getLows().pop(), stockData.getCloses().pop(), stockData.getVolumes().pop());
            }

            CandlestickRenderer renderer = new CandlestickRenderer();
            DefaultOHLCDataset dataSet = new DefaultOHLCDataset(1, data);
            chart = ChartFactory.createCandlestickChart(symbol, "Date", "Price", dataSet, false);
            plot = chart.getXYPlot();
            DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
            NumberAxis rangeAxis = new NumberAxis();
            renderer.setSeriesPaint(0, Color.BLACK);
            renderer.setDrawVolume(true);
            DateTickUnit tickUnit = new DateTickUnit(DateTickUnit.DAY, 1);
            SimpleDateFormat customDateFormat = new SimpleDateFormat("MMM");
            ;
            // Set dateFormat depending on TimeSeries
            String dateFormat;
            ;
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
                SegmentedTimeline fifteenTimeline = SegmentedTimeline.newFifteenMinuteTimeline();
                // This sets the timeline as a Mon-Fri 9am-4pm timeframe
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
            chart.getXYPlot().getRangeAxis().setRange(lowestLow * 0.95, highestHigh * 1.05);
            plot.setOrientation(PlotOrientation.VERTICAL);
            chart.setAntiAlias(false);


            // Now create the chart and chart panel
            plot.setRenderer(renderer);

            chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(600, 300));
            chartPanel.setMouseZoomable(true);
            chartPanel.setMouseWheelEnabled(true);

            // Mouse listener
            chartPanel.addChartMouseListener(this);
            CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
            xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
            xCrosshair.setLabelVisible(true);
            yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
            yCrosshair.setLabelVisible(true);
            crosshairOverlay.addDomainCrosshair(xCrosshair);
            crosshairOverlay.addRangeCrosshair(yCrosshair);
            chartPanel.addOverlay(crosshairOverlay);
            chartPanel.setFillZoomRectangle(true);
            chartTab.add(chartPanel, BorderLayout.NORTH);
            chartTab.validate();
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
    }

    // The following functions will display an axis of the price
    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        // ignore
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        Rectangle2D dataArea = this.chartPanel.getScreenDataArea();
        chart = event.getChart();
        plot = (XYPlot) chart.getPlot();
        ValueAxis xAxis = plot.getDomainAxis();
        double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, RectangleEdge.BOTTOM);
        double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
        this.xCrosshair.setValue(x);
        this.yCrosshair.setValue(y);
    }


    // These functions will get the highest/lowest values of the prices
    protected double getLowestLow(DefaultOHLCDataset dataset) {
        double lowest = dataset.getLowValue(0, 0);
        for (int i = 1; i < dataset.getItemCount(0); i++) {
            if (dataset.getLowValue(0, i) < lowest) {
                lowest = dataset.getLowValue(0, i);
            }
        }

        return lowest;
    }

    protected double getHighestHigh(DefaultOHLCDataset dataset) {
        double highest = dataset.getHighValue(0, 0);
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
                // Remove the chart Panel
                contentPane.remove(component);
                contentPane.repaint();
                contentPane.revalidate();
            }
        }
    }
}
