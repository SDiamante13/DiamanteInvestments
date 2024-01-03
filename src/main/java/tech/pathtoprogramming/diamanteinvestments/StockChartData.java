package tech.pathtoprogramming.diamanteinvestments;

import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.*;

public class StockChartData {

    private final Deque<Date> dates = new ArrayDeque<>();
    private final Deque<Double> opens = new ArrayDeque<>();
    private final Deque<Double> highs = new ArrayDeque<>();
    private final Deque<Double> lows = new ArrayDeque<>();
    private final Deque<Double> closes = new ArrayDeque<>();
    private final Deque<Double> volumes = new ArrayDeque<>();
    private final TimeFrame timeSeries;
    private final Interval interval;
    Date date;

    public StockChartData() {
        this.timeSeries = TimeFrame.DAILY;
        this.interval = Interval.SIXTY;
    }

    public StockChartData(TimeFrame timeSeries, String symbol, Interval interval) {
        this.timeSeries = timeSeries;
        this.interval = interval;

        String url = "https://www.alphavantage.co/query?function=" + this.timeSeries
                + "&symbol=" + symbol
                + "&interval=" + this.interval
                + "&apikey=NKNKJCBRLYI9H5SO&datatype=csv";

        // Alpha Advantage returns a csv file with dates, opens, highs, lows, closes, volumes
        try {
            URL alphaAdvantage = new URL(url);
            URLConnection data = alphaAdvantage.openConnection();
            Scanner input = new Scanner(data.getInputStream());
            if (input.hasNext()) { // skip header line
                input.nextLine();
            }
            // read in data
            while (input.hasNextLine()) {
                readCsvData_addDataToInstanceVariables(input);
            }

            input.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void readCsvData_addDataToInstanceVariables(Scanner input) {
        readLine(input.nextLine());
    }

    void readLine(String line) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

        int target = line.indexOf('-');
        // Grab year
        int year = Integer.parseInt(line.substring(0, target));
        // Grab month
        int start = target++ + 1;
        target = line.indexOf('-', target);
        int month = Integer.parseInt(line.substring(start, target));

        int day;
        if (this.timeSeries != TimeFrame.INTRADAY) {
            // Grab day for timeseries other than INTRADAY
            start = target++ + 1;
            target = line.indexOf(',', target);
            day = Integer.parseInt(line.substring(start, target));
        } else {
            // Grab day for INTRADAY
            start = target++ + 1;
            target = line.indexOf(' ', target);
            day = Integer.parseInt(line.substring(start, target));
        }
        // Grab hours, min, & sec for INTRADAY
        if (this.timeSeries == TimeFrame.INTRADAY) {
            start = target++ + 1;
            target = line.indexOf(':', target);
            int hour = Integer.parseInt(line.substring(start, target));
            start = target++ + 1;
            target = line.indexOf(':', target);
            int min = Integer.parseInt(line.substring(start, target));
            start = target++ + 1;
            target = line.indexOf(',', target);
            int sec = Integer.parseInt(line.substring(start, target));
            date = new Date(year, month, day, hour, min, sec);
        } else {
            date = new Date(year, month, day);
        }
        dates.addLast(date);

        // addLast open prices
        start = target++ + 1;
        target = getTarget(line, target);
        // The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
        String tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
        tempPrice = tempPrice.substring(1); // trim off $
        opens.addLast(Double.parseDouble(tempPrice));

        // addLast high prices
        start = target++ + 1;
        target = getTarget(line, target);
        // The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
        tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
        tempPrice = tempPrice.substring(1); // trim off $
        highs.addLast(Double.parseDouble(tempPrice));

        // addLast low prices
        start = target++ + 1;
        target = getTarget(line, target);
        // The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
        tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
        tempPrice = tempPrice.substring(1); // trim off $
        lows.addLast(Double.parseDouble(tempPrice));

        // addLast close prices
        start = target++ + 1;
        target = getTarget(line, target);
        // The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
        String closeString = getSubstringForClosePrices(line, currencyFormatter, target, start);
        double close = Double.parseDouble(closeString);

        closes.addLast(close);

        double volume = parseVolume(line, target + 1);
        volumes.addLast(volume);
    }

    private int getTarget(String line, int target) {
        return line.indexOf(",", target);
    }

    private String getSubstringForClosePrices(String line, NumberFormat currencyFormatter, int target, int start) {
        double close = Double.parseDouble(line.substring(start, target));
        return currencyFormatter.format(close)
                .substring(1);
    }

    private double parseVolume(String line, int start) {
        String volume = line.substring(start, line.length() - 1);
        return Double.parseDouble(volume);
    }

    public Deque<Date> getDates() {
        return dates;
    }

    public Deque<Double> getOpens() {
        return opens;
    }

    public Deque<Double> getHighs() {
        return highs;
    }

    public Deque<Double> getLows() {
        return lows;
    }

    public Deque<Double> getCloses() {
        return closes;
    }

    public Deque<Double> getVolumes() {
        return volumes;
    }

    public TimeFrame getTimeFrame() {
        return timeSeries;
    }

    public Interval getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return "StockChartData{" +
                "dates=" + dates +
                ", opens=" + opens +
                ", highs=" + highs +
                ", lows=" + lows +
                ", closes=" + closes +
                ", volumes=" + volumes +
                ", timeSeries=" + timeSeries +
                ", interval=" + interval +
                ", date=" + date +
                '}';
    }
}

