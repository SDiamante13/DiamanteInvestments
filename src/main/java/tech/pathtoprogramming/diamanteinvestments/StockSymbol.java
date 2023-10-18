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
            String substring = line.substring(deci + 1, end);
            stockName = substring;

            price = parsePrice(doc);

            change = parseChangeInDollars(doc);
            changePercent = parseChangeInPercentage(doc);
            close = parseClose(doc);

            line = doc.select("li.kv__item").toString();
            open = parseOpen(line);
            parseLowAndHigh_assignInstanceVariables(line);
            parseYearlyHighAndLow_assignInstanceVariables(line);
            marketCap = parseMarketCap(line);
            peRatio = parsePERatio(line);
            eps = parseEarningsPerShare(line);
            floatShorted = parsePercentageFloatShorted(line);
            averageVolume = parseAverageVolume(line);

            volume = parseTodaysVolume(doc);

            callApi_calculate50DayAnd100DayMovingAverages_assignInstanceVariables(symbol);

            iconUrl = new URL("https://www.google.com/webhp");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String parsePrice(Document doc) {
        String line = doc.select("div.intraday__data").toString();
        int target = line.indexOf("lastsale");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, deci + 3);
    }

    private String parseChangeInDollars(Document doc) {
        String line = doc.select("span.change--point--q").toString();
        int target = line.indexOf("after");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, deci + 3).trim();
    }

    private String parseChangeInPercentage(Document doc) {
        Elements ele = doc.select("span.change--percent--q");
        String line = ele.toString();
        int target = line.indexOf("after");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, deci + 3).trim();
    }

    private String parseClose(Document doc) {
        Elements ele = doc.select("tbody.remove-last-border");
        String line = ele.toString();
        int target = line.indexOf("semi");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '$') {
            start--;
        }
        return line.substring(start + 1, deci + 3);
    }

    private String parseOpen(String line) {
        int deci = line.indexOf(".");
        int start = deci;
        while (line.charAt(start) != '$') {
            start--;
        }
        return line.substring(start + 1, deci + 3);
    }

    private void parseLowAndHigh_assignInstanceVariables(String line) {
        int deci;
        int start;
        int target;
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

    private void parseYearlyHighAndLow_assignInstanceVariables(String line) {
        int index;
        int start2;
        int start;
        int deci;
        int target;
        //------------------------------------------
        // Find 52 week low and high
        target = line.indexOf("52 Week Range");
        deci = line.indexOf(".", target);
        start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        yearlyLow = line.substring(start + 1, deci + 3);
        index = deci + 1;
        // Find the decimal of the high
        index = line.indexOf(".", index);
        start2 = index;
        while (line.charAt(start2) != '-') {
            start2--;
        }
        yearlyHigh = line.substring(start2 + 1, index + 3);
    }

    private String parseMarketCap(String line) {
        int target = line.indexOf("Market Cap");
        int deci = line.indexOf(".", target);
        int start = deci;
        if (deci - target > 200) { // ETFs will show N/A
            return "N/A";
        }
        while (line.charAt(start) != '$') {
            start--;
        }
        String result = line.substring(start + 1, deci + 4); // increased length to get the B, M, or K
        if (result.endsWith("<")) {
            result = result.substring(0, result.length() - 2);
        }
        if (!result.endsWith("B") && !result.endsWith("M") && !result.endsWith("K")) {
            result = "N/A";
        }
        return result;
    }

    private String parsePERatio(String line) {
        int target = line.indexOf("P/E Ratio");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        if ((deci - target) < 70) {
            return line.substring(start + 1, deci + 3);
        }
        return "N/A";
    }

    private String parseEarningsPerShare(String line) {
        int target = line.indexOf("EPS");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '$') {
            start--;
        }
        if ((deci - target) < 70) { // if N/A then keep EPS as N/A
            return line.substring(start + 1, deci + 3);
        }
        return "N/A";
    }

    private String parsePercentageFloatShorted(String line) {
        int target = line.indexOf("Float Shorted");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        if ((deci - target) < 70) {
            return line.substring(start + 1, deci + 3);
        }
        return "N/A";
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
