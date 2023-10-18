package tech.pathtoprogramming.diamanteinvestments;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.Scanner;

public class StockSymbol {

    private String stockName = "not found";
    private String price = "not found";
    private String change = "not found";
    private String changePercent = "not found";

    private String close = "not found";
    private String open = "not found";
    private String low = "not found";
    private String high = "not found";
    private String yearlyLow = "not found";
    private String yearlyHigh = "not found";
    private String marketCap = "N/A";
    private String peRatio = "N/A";
    private String eps = "N/A";
    private String floatShorted = "N/A";
    private String volume = "not found";
    private String averageVolume = "not found";
    private String fiftyDayMA = "not found";
    private String hundredDayMA = "not found";
    private URL iconUrl;


    public StockSymbol(String symbol) {
        try {
            Document doc = Jsoup.connect("https://www.marketwatch.com/investing/stock/" + symbol).get();

            //------------------------------------------
            // Find the stock name
            // h1> class = company__name
            // use div# for id
            Elements ele = doc.select("h1.company__name");
            String line = ele.toString();
            int target = line.indexOf("name");
            int deci = line.indexOf(">", target);
            int end = line.indexOf("<", deci);
            if (line.isEmpty()) { // The stock symbol is invalid so end method
                return;
            }
            stockName = line.substring(deci + 1, end);

            //------------------------------------------
            // Find current price of stock
            // class = intraday_data
            // use div# for id
            ele = doc.select("div.intraday__data");
            //change--point--q
            line = ele.toString();
            target = line.indexOf("lastsale");
            deci = line.indexOf(".", target);
            int start = deci;
            while (line.charAt(start) != '>') {
                start--;
            }
            price = line.substring(start + 1, deci + 3);

            //------------------------------------------ ERRORS HERE for when market is open
            // Find change of stock today in $
            // class = span.change--point--q
            ele = doc.select("span.change--point--q");
            line = ele.toString();
            target = line.indexOf("after");
            deci = line.indexOf(".", target);
            start = deci;
            while (line.charAt(start) != '>') {
                start--;
            }
            change = line.substring(start + 1, deci + 3).trim();

            //------------------------------------------
            // Find change of stock today in %
            // span class = change=--percent--q
            ele = doc.select("span.change--percent--q");
            line = ele.toString();
            target = line.indexOf("after");
            deci = line.indexOf(".", target);
            start = deci;
            while (line.charAt(start) != '>') {
                start--;
            }
            changePercent = line.substring(start + 1, deci + 3).trim();


            //------------------------------------------
            // Find close of the stock
            ele = doc.select("tbody.remove-last-border");
            line = ele.toString();
            target = line.indexOf("semi");
            deci = line.indexOf(".", target);
            start = deci;
            while (line.charAt(start) != '$') {
                start--;
            }
            close = line.substring(start + 1, deci + 3);


            //------------------------------------------
            // Find open of the stock
            ele = doc.select("li.kv__item");
            line = ele.toString();
            deci = line.indexOf(".");
            start = deci;
            while (line.charAt(start) != '$') {
                start--;
            }
            open = line.substring(start + 1, deci + 3);


            //------------------------------------------
            // Find low and high of the stock
            target = line.indexOf("Day Range");
            deci = line.indexOf(".", target);
            start = deci;
            int index = deci + 1;
            while (line.charAt(start) != '>') {
                start--;
            }
            low = line.substring(start + 1, deci + 3);
            // Find the decimal of the high
            index = line.indexOf(".", index);
            int start2 = index;
            while (line.charAt(start2) != '-') {
                start2--;
            }
            high = line.substring(start2 + 1, index + 3);

            //------------------------------------------
            // Find 52 week low and high
            target = line.indexOf("52 Week Range");
            deci = line.indexOf(".", target);
            start = deci;
            index = deci + 1;
            while (line.charAt(start) != '>') {
                start--;
            }
            yearlyLow = line.substring(start + 1, deci + 3);
            // Find the decimal of the high
            index = line.indexOf(".", index);
            start2 = index;
            while (line.charAt(start2) != '-') {
                start2--;
            }
            yearlyHigh = line.substring(start2 + 1, index + 3);

            //------------------------------------------
            // Find the Market Cap
            target = line.indexOf("Market Cap");
            deci = line.indexOf(".", target);
            start = deci;
            if (deci - target > 200) { // ETFs will show N/A
                marketCap = "N/A";
            } else {
                while (line.charAt(start) != '$') {
                    start--;
                }
                marketCap = line.substring(start + 1, deci + 4); // increased length to get the B, M, or K
                if (marketCap.endsWith("<")) {
                    marketCap = marketCap.substring(0, marketCap.length() - 2);
                }
                if (!marketCap.endsWith("B") && !marketCap.endsWith("M") && !marketCap.endsWith("K")) {
                    marketCap = "N/A";
                }
            }

            //------------------------------------------
            // Find the P/E Ratio
            target = line.indexOf("P/E Ratio");
            deci = line.indexOf(".", target);
            start = deci;
            while (line.charAt(start) != '>') {
                start--;
            }
            if ((deci - target) < 70) { // if N/A then keep P/E as N/A
                peRatio = line.substring(start + 1, deci + 3);
            }

            //------------------------------------------
            // Find the EPS (Earnings per Share)
            target = line.indexOf("EPS");
            deci = line.indexOf(".", target);
            start = deci;
            while (line.charAt(start) != '$') {
                start--;
            }
            if ((deci - target) < 70) { // if N/A then keep EPS as N/A
                eps = line.substring(start + 1, deci + 3);
            }

            //------------------------------------------
            // Find the % of float shorted
            target = line.indexOf("Float Shorted");
            deci = line.indexOf(".", target);
            start = deci;
            while (line.charAt(start) != '>') {
                start--;
            }
            if ((deci - target) < 70) { // if N/A then keep float shorted as N/A
                floatShorted = line.substring(start + 1, deci + 3);
            }

            averageVolume = parseAverageVolume(line);
            volume = parseTodaysVolume(doc);

            callApi_calculate50DayAnd100DayMovingAverages_assignInstanceVariables(symbol);

            iconUrl = new URL("https://www.google.com/webhp");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String parseAverageVolume(String line) {
        int target = line.indexOf("Average Volume");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        String temp = line.substring(start + 1, deci + 4);
        if (temp.charAt(temp.length() - 1) == '<') {
            return temp.substring(0, temp.length() - 1);
        } else {
            return temp;
        }
    }

    private String parseTodaysVolume(Document doc) {
        String line = doc.select("div.range__header").toString();
        int target = line.indexOf("Volume");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, deci + 4);
    }

    private void callApi_calculate50DayAnd100DayMovingAverages_assignInstanceVariables(String symbol) throws IOException {
        String alphaUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY"
                + "&symbol=" + symbol
                + "&apikey=NKNKJCBRLYI9H5SO&datatype=csv";
        URL alphaAdvantage = new URL(alphaUrl);
        URLConnection data = alphaAdvantage.openConnection();
        Scanner input = new Scanner(data.getInputStream());
        if (input.hasNext()) { // skip header line
            input.nextLine();
        }
        double[] closeValues = new double[100];
        int x = 0;
        int count = 0;
        double sum50 = 0;
        double sum100 = 0;
        double fiftyAvg;
        double hundredAvg;
        int aTarget;
        int aDeci;
        int aStart;
        String aClose;
        while (input.hasNextLine()) {
            String aLine = input.nextLine();
            aTarget = aLine.lastIndexOf(",");
            aDeci = aTarget - 5; // move target to the decimal point
            aStart = aDeci;
            while (aLine.charAt(aStart) != ',') {
                aStart--;
            }
            aClose = aLine.substring(aStart + 1, aDeci + 3);
            closeValues[x] = Double.parseDouble(aClose); // convert String to int and store into array
            x++;
        }
        for (double cV : closeValues) {
            if (count < 50) {
                sum50 += cV;
            } else {
                sum100 += cV;
            }
            count++;
        }
        fiftyAvg = sum50 / 50;
        // trim to 2 decimal places
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        String temp = currencyFormatter.format(fiftyAvg);
        temp = temp.substring(1); // trim off $
        fiftyDayMA = temp;
        // trim to 2 decimal places
        sum100 += sum50;
        hundredAvg = sum100 / closeValues.length;
        // trim to 2 decimal places
        temp = currencyFormatter.format(hundredAvg);
        temp = temp.substring(1); // trim off $
        hundredDayMA = temp;
        input.close();
    }

    public String getStockName() {
        return stockName;
    }

    public String getPrice() {
        return price;
    }

    public String getChange() {
        return change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public String getClose() {
        return close;
    }

    public String getOpen() {
        return open;
    }

    public String getLow() {
        return low;
    }

    public String getHigh() {
        return high;
    }

    public String getYearlyLow() {
        return yearlyLow;
    }

    public String getYearlyHigh() {
        return yearlyHigh;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public String getPeRatio() {
        return peRatio;
    }

    public String getEps() {
        return eps;
    }

    public String getFloatShorted() {
        return floatShorted;
    }


    public String getVolume() {
        return volume;
    }


    public String getAverageVolume() {
        return averageVolume;
    }


    public String getFiftyDayMA() {
        return fiftyDayMA;
    }


    public String getHundredDayMA() {
        return hundredDayMA;
    }

    public URL getIconUrl() {
        return iconUrl;
    }
}
