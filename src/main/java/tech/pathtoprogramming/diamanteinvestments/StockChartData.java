package tech.pathtoprogramming.diamanteinvestments;

import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.*;

public class StockChartData {

    private final Deque<Date> dates;
    private final Deque<Double> opens;
    private final Deque<Double> highs;
    private final Deque<Double> lows;
    private final Deque<Double> closes;
    private final Deque<Double> volumes;
    private final TimeFrame timeSeries;
    private final Interval interval;
    Date date;

    public StockChartData(TimeFrame timeSeries, String symbol, Interval interval) {
        this.timeSeries = timeSeries;
        this.interval = interval;

        dates = new ArrayDeque<>();
        opens = new ArrayDeque<>();
        highs = new ArrayDeque<>();
        lows = new ArrayDeque<>();
        closes = new ArrayDeque<>();
        volumes = new ArrayDeque<>();
        int target;
        int year;
        int month;
        int day;
        int hour;
        int min;
        int sec;
        int start;
        String tempPrice;

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
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
                String line = input.nextLine();
                // push dates
                target = line.indexOf('-');
                // Grab year
                year = Integer.parseInt(line.substring(0, target));
                // Grab month
                start = target++ + 1;
                target = line.indexOf('-', target);
                month = Integer.parseInt(line.substring(start, target));
                if (timeSeries != TimeFrame.INTRADAY) {
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
                if (timeSeries == TimeFrame.INTRADAY) {
                    start = target++ + 1;
                    target = line.indexOf(':', target);
                    hour = Integer.parseInt(line.substring(start, target));
                    start = target++ + 1;
                    target = line.indexOf(':', target);
                    min = Integer.parseInt(line.substring(start, target));
                    start = target++ + 1;
                    target = line.indexOf(',', target);
                    sec = Integer.parseInt(line.substring(start, target));
                    date = new Date(year, month, day, hour, min, sec);
                } else {
                    date = new Date(year, month, day);
                }
                dates.addLast(date);

                // addLast open prices
                start = target++ + 1;
                target = line.indexOf(",", target);
                // The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
                tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
                tempPrice = tempPrice.substring(1); // trim off $
                opens.addLast(Double.parseDouble(tempPrice));

                // addLast high prices
                start = target++ + 1;
                target = line.indexOf(",", target);
                // The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
                tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
                tempPrice = tempPrice.substring(1); // trim off $
                highs.addLast(Double.parseDouble(tempPrice));

                // addLast low prices
                start = target++ + 1;
                target = line.indexOf(",", target);
                // The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
                tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
                tempPrice = tempPrice.substring(1); // trim off $
                lows.addLast(Double.parseDouble(tempPrice));

                // addLast close prices
                start = target++ + 1;
                target = line.indexOf(",", target);
                // The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
                tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
                tempPrice = tempPrice.substring(1); // trim off $
                closes.addLast(Double.parseDouble(tempPrice));

                // addLast volumes
                start = target++ + 1;
                target = line.length() - 1;
                volumes.addLast(Double.parseDouble(line.substring(start, target)));
            }

            input.close();
        } catch (Exception e) {
            System.out.println(e);
        }
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

