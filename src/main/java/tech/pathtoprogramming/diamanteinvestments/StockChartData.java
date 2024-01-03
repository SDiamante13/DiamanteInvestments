package tech.pathtoprogramming.diamanteinvestments;

import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Stack;

public class StockChartData {

    private Stack<Date> dates;
    private Stack<Double> opens;
    private Stack<Double> highs;
    private Stack<Double> lows;
    private Stack<Double> closes;
    private Stack<Double> volumes;
    private TimeFrame timeSeries;
    private String symbol;
    private Interval interval;
    Date date;

    // Creating enum for TIME_SERIES selection
    public enum TimeFrame {
        INTRADAY {
            public String toString() {
                return "TIME_SERIES_INTRADAY";
            }
        },
        DAILY {
            public String toString() {
                return "TIME_SERIES_DAILY";
            }
        },
        WEEKLY {
            public String toString() {
                return "TIME_SERIES_WEEKLY";
            }
        },
        MONTHLY {
            public String toString() {
                return "TIME_SERIES_MONTHLY";
            }
        }
    }

    // Creating enum for Time interval (only applicable to INTRADAY)
    public enum Interval {
        ONE {
            public String toString() {
                return "1min";
            }
        },
        FIVE {
            public String toString() {
                return "5min";
            }
        },
        FIFTEEN {
            public String toString() {
                return "15min";
            }
        },
        THIRTY {
            public String toString() {
                return "30min";
            }
        },
        SIXTY {
            public String toString() {
                return "60min";
            }
        }
    }

    // constructor
    public StockChartData(TimeFrame timeSeries, String symbol, Interval interval) {
        this.timeSeries = timeSeries;
        this.symbol = symbol;
        this.interval = interval;

        dates = new Stack<>();
        opens = new Stack<>();
        highs = new Stack<>();
        lows = new Stack<>();
        closes = new Stack<>();
        volumes = new Stack<>();
        int target = 0;
        int year = 0, month = 0, day = 1, hour = 1, min = 1, sec = 1;
        int start = 0;
        String tempPrice = "0";


        String url = "https://www.alphavantage.co/query?function=" + this.timeSeries
                + "&symbol=" + this.symbol
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
                year = Integer.valueOf(line.substring(0, target));
                // Grab month
                start = target++ + 1;
                target = line.indexOf('-', target);
                month = Integer.valueOf(line.substring(start, target));
                if (timeSeries != TimeFrame.INTRADAY) {
                    // Grab day for timeseries other than INTRADAY
                    start = target++ + 1;
                    target = line.indexOf(',', target);
                    day = Integer.valueOf(line.substring(start, target));
                } else {
                    // Grab day for INTRADAY
                    start = target++ + 1;
                    target = line.indexOf(' ', target);
                    day = Integer.valueOf(line.substring(start, target));
                }
                // Grab hours, min, & sec for INTRADAY
                if (timeSeries == TimeFrame.INTRADAY) {
                    start = target++ + 1;
                    target = line.indexOf(':', target);
                    hour = Integer.valueOf(line.substring(start, target));
                    start = target++ + 1;
                    target = line.indexOf(':', target);
                    min = Integer.valueOf(line.substring(start, target));
                    start = target++ + 1;
                    target = line.indexOf(',', target);
                    sec = Integer.valueOf(line.substring(start, target));
                    date = new Date(year, month, day, hour, min, sec);
                } else {
                    date = new Date(year, month, day);
                }
                //2018-02-16 00:00:00,
                dates.push(date);


                // push open prices
                start = target++ + 1;
                target = line.indexOf(",", target);
                // The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
                tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
                tempPrice = tempPrice.substring(1, tempPrice.length()); // trim off $
                opens.push(Double.parseDouble(tempPrice));

                // push high prices
                start = target++ + 1;
                target = line.indexOf(",", target);
                // The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
                tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
                tempPrice = tempPrice.substring(1, tempPrice.length()); // trim off $
                highs.push(Double.parseDouble(tempPrice));

                // push low prices
                start = target++ + 1;
                target = line.indexOf(",", target);
                // The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
                tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
                tempPrice = tempPrice.substring(1, tempPrice.length()); // trim off $
                lows.push(Double.parseDouble(tempPrice));

                // push close prices
                start = target++ + 1;
                target = line.indexOf(",", target);
                // The purpose of this is to trim the trailing zeros i.e. 258.5800 --> $258.58 --> 258.58
                tempPrice = currencyFormatter.format(Double.parseDouble(line.substring(start, target)));
                tempPrice = tempPrice.substring(1, tempPrice.length()); // trim off $
                closes.push(Double.parseDouble(tempPrice));

                // push volumes
                start = target++ + 1;
                target = line.length() - 1;
                volumes.push(Double.parseDouble(line.substring(start, target)));
            }

            input.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Stack<Date> getDates() {
        return dates;
    }

    public Stack<Double> getOpens() {
        return opens;
    }

    public Stack<Double> getHighs() {
        return highs;
    }

    public Stack<Double> getLows() {
        return lows;
    }

    public Stack<Double> getCloses() {
        return closes;
    }

    public Stack<Double> getVolumes() {
        return volumes;
    }

    public TimeFrame getTimeFrame() {
        return timeSeries;
    }

    public String getSymbol() {
        return symbol;
    }

    public Interval getInterval() {
        return interval;
    }

}

