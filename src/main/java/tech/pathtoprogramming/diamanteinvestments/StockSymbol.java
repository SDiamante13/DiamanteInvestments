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

            stockName = parseStockName(doc);
            price = parsePrice(doc);
            change = parseChangeInDollars(doc);
            changePercent = parseChangePercent(doc);
            close = parseClose(doc);
            volume = parseTodaysVolume(doc); // increased length to get the B, M, or K
            iconUrl = new URL("https://www.google.com/webhp");

            String line = doc.select("li.kv__item").toString();
            open = parseOpen(line);
            parseLowAndHigh(line);
            parse52WeekHighAndLow(line);
            marketCap = parseMarketCap(line);
            peRatio = parsePERatio(line);
            eps = parseEarningsPerShare(line);
            floatShorted = parsePercentageOfFloatShorted(line);
            averageVolume = parseAverageVolume(line);

            calculate50DayAnd100DayMovingAverages(symbol);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String parseStockName(Document doc) {
        // h1> class = company__name
        // use div# for id
        Elements ele = doc.select("h1.company__name");
        String line = ele.toString();
        int target = line.indexOf("name");
        int deci = line.indexOf(">", target);
        int end = line.indexOf("<", deci);
        if (line.isEmpty()) {
            throw new IllegalArgumentException("The stock symbol is invalid");
        }
        return line.substring(deci + 1, end);
    }

    private String parsePrice(Document doc) {
        int target;
        Elements ele;
        String line;
        int deci;
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
        return line.substring(start + 1, deci + 3);
    }

    private String parseChangeInDollars(Document doc) {
        int start;
        int target;
        String line;
        Elements ele;
        int deci;
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
        return line.substring(start + 1, deci + 3).trim();
    }

    private String parseChangePercent(Document doc) {
        String line;
        Elements ele;
        int target;
        int deci;
        int start;
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
        return line.substring(start + 1, deci + 3).trim();
    }

    private String parseClose(Document doc) {
        int deci;
        Elements ele;
        String line;
        int start;
        int target;
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
        return line.substring(start + 1, deci + 3);
    }

    private String parseOpen(String line) {
        int start;
        int deci;
        deci = line.indexOf(".");
        start = deci;
        while (line.charAt(start) != '$') {
            start--;
        }
        return line.substring(start + 1, deci + 3);
    }

    private void parseLowAndHigh(String line) {
        int deci;
        int target;
        int start;
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
    }

    private void parse52WeekHighAndLow(String line) {
        int index;
        int start;
        int start2;
        int deci;
        int target;
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
    }

    private String parseMarketCap(String line) {
        int deci;
        int target;
        int start;
        //------------------------------------------
        // Find the Market Cap
        target = line.indexOf("Market Cap");
        deci = line.indexOf(".", target);
        start = deci;
        String temp;
        if (deci - target <= 200) {
            while (line.charAt(start) != '$') {
                start--;
            }
            temp = line.substring(start + 1, deci + 4); // increased length to get the B, M, or K
            if (temp.endsWith("<")) {
                temp = temp.substring(0, temp.length() - 2);
            }
            if (!temp.endsWith("B") && !temp.endsWith("M") && !temp.endsWith("K")) {
                temp = "N/A";
            }
        } else { // ETFs will show N/A
            temp = "N/A";
        }
        return temp;
    }

    private String parsePERatio(String line) {
        int target;
        int deci;
        int start;
        target = line.indexOf("P/E Ratio");
        deci = line.indexOf(".", target);
        start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        if ((deci - target) < 70) { // if N/A then keep P/E as N/A
            return line.substring(start + 1, deci + 3);
        } else {
            return "N/A";
        }
    }

    private String parseEarningsPerShare(String line) {
        int deci;
        int start;
        int target;
        //------------------------------------------
        // Find the EPS (Earnings per Share)
        target = line.indexOf("EPS");
        deci = line.indexOf(".", target);
        start = deci;
        while (line.charAt(start) != '$') {
            start--;
        }
        if ((deci - target) < 70) { // if N/A then keep EPS as N/A
            return line.substring(start + 1, deci + 3);
        } else {
            return "N/A";
        }
    }

    private String parsePercentageOfFloatShorted(String line) {
        int start;
        int target;
        int deci;
        target = line.indexOf("Float Shorted");
        deci = line.indexOf(".", target);
        start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        if ((deci - target) < 70) {
            return line.substring(start + 1, deci + 3);
        } else {
            return "N/A";
        }
    }

    private String parseAverageVolume(String line) {
        int deci;
        int start;
        int target;
        //------------------------------------------
        // Find the Average Volume
        target = line.indexOf("Average Volume");
        deci = line.indexOf(".", target);
        start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        String averageVolume = line.substring(start + 1, deci + 4); // increased length to get the B, M, or K
        if (averageVolume.charAt(averageVolume.length() - 1) == '<') {
            averageVolume = averageVolume.substring(0, averageVolume.length() - 1);
        }
        return averageVolume;
    }

    private String parseTodaysVolume(Document doc) {
        int target;
        Elements ele;
        String line;
        int deci;
        int start;
        //------------------------------------------
        // Find today's volume
        ele = doc.select("div.range__header");
        line = ele.toString();
        target = line.indexOf("Volume");
        deci = line.indexOf(".", target);
        start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, deci + 4);
    }

    private void calculate50DayAnd100DayMovingAverages(String symbol) throws IOException {
        String alphaUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY"
                + "&symbol=" + symbol
                + "&apikey=NKNKJCBRLYI9H5SO&datatype=csv";
        URL alphaAdvantage = new URL(alphaUrl);
        URLConnection data = alphaAdvantage.openConnection();
        Scanner input = new Scanner(data.getInputStream());
        if (input.hasNext()) { // skip header line
            input.nextLine();
        }
        // read in close values to a fixed array
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
            closeValues[x] = Double.parseDouble(aClose);
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
        String temp;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        temp = currencyFormatter.format(fiftyAvg);
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
