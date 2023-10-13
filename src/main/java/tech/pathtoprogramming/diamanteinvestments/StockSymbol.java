package tech.pathtoprogramming.diamanteinvestments;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

            String stockNameHtml = doc.select("h1.company__name").toString();
            String priceHtml = doc.select("div.intraday__data").toString();
            String changeInDollarsHtml = doc.select("span.change--point--q").toString();
            String changeInPercentageHtml = doc.select("span.change--percent--q").toString();
            String closeHtml = doc.select("tbody.remove-last-border").toString();
            String todaysVolumeHtml = doc.select("div.range__header").toString();
            stockName = parseStockName(stockNameHtml);
            price = parsePrice(priceHtml);
            change = parseChangeInDollars(changeInDollarsHtml);
            changePercent = parseChangePercent(changeInPercentageHtml);
            close = parseClose(closeHtml);
            volume = parseTodaysVolume(todaysVolumeHtml);
            iconUrl = new URL("https://www.google.com/webhp");

            String line = doc.select("li.kv__item").toString();
            open = parseOpen(line);
            low = parseLow(line);
            high = parseHigh(line);
            yearlyLow = parseYearlyLow(line);
            yearlyHigh = parseYearlyHigh(line);
            marketCap = parseMarketCap(line);
            peRatio = parsePERatio(line);
            eps = parseEarningsPerShare(line);
            floatShorted = parsePercentageOfFloatShorted(line);
            averageVolume = parseAverageVolume(line);

            double[] closeValues = getCloseValues(symbol);
            CloseValueSums closeValueSums = calculateCloseValueSums(closeValues);
            fiftyDayMA = parseFiftyDayMovingAverage(closeValueSums);
            hundredDayMA = parseOneHundredDayMovingAverage(closeValues, closeValueSums);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String parseStockName(String stockNameHtml) {
        // h1> class = company__name
        // use div# for id
        int target = getYearlyTarget(stockNameHtml, "name");
        int deci = stockNameHtml.indexOf(">", target);
        int end = stockNameHtml.indexOf("<", deci);
        if (stockNameHtml.isEmpty()) {
            throw new IllegalArgumentException("The stock symbol is invalid");
        }
        return stockNameHtml.substring(deci + 1, end);
    }

    private String parsePrice(String priceHtml) {
        // class = intraday_data
        // use div# for id
        //change--point--q
        int target = getYearlyTarget(priceHtml, "lastsale");
        int deci = getDeci(priceHtml, target);
        int start = deci;
        while (priceHtml.charAt(start) != '>') {
            start--;
        }
        return priceHtml.substring(start + 1, deci + 3);
    }

    private String parseChangeInDollars(String changeInDollarsHtml) {
        //------------------------------------------ ERRORS HERE for when market is open
        // Find change of stock today in $
        // class = span.change--point--q
        int target = getYearlyTarget(changeInDollarsHtml, "after");
        int deci = getDeci(changeInDollarsHtml, target);
        int start = deci;
        while (changeInDollarsHtml.charAt(start) != '>') {
            start--;
        }
        return changeInDollarsHtml.substring(start + 1, deci + 3).trim();
    }

    private String parseChangePercent(String changeInPercentageHtml) {
        // span class = change=--percent--q
        int target = getYearlyTarget(changeInPercentageHtml, "after");
        int deci = getDeci(changeInPercentageHtml, target);
        int start = deci;
        while (changeInPercentageHtml.charAt(start) != '>') {
            start--;
        }
        return changeInPercentageHtml.substring(start + 1, deci + 3).trim();
    }

    private String parseClose(String closeHtml) {
        int target = getYearlyTarget(closeHtml, "semi");
        int deci = getDeci(closeHtml, target);
        int start = deci;
        while (closeHtml.charAt(start) != '$') {
            start--;
        }
        return closeHtml.substring(start + 1, deci + 3);
    }

    private String parseOpen(String line) {
        int start;
        int deci;
        deci = getYearlyTarget(line, ".");
        start = deci;
        while (line.charAt(start) != '$') {
            start--;
        }
        return line.substring(start + 1, deci + 3);
    }

    private String parseHigh(String line) {
        int index = getDeci(line, getYearlyTarget(line, "Day Range")) + 1;
        index = getDeci(line, index);
        int start2 = index;
        while (line.charAt(start2) != '-') {
            start2--;
        }
        return line.substring(start2 + 1, index + 3);
    }

    private String parseLow(String line) {
        int start = getDeci(line, getYearlyTarget(line, "Day Range"));
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, getDeci(line, getYearlyTarget(line, "Day Range")) + 3);
    }

    private String parseYearlyHigh(String line) {
        // Find the decimal of the high
        int index = getDeci(line, getYearlyTarget(line, "52 Week Range")) + 1;
        index = getDeci(line, index);
        int start2 = index;
        while (line.charAt(start2) != '-') {
            start2--;
        }
        String substring = line.substring(start2 + 1, index + 3);
        return substring;
    }

    private String parseYearlyLow(String line) {
        //------------------------------------------
        // Find 52 week low and high
        int start = getDeci(line, getYearlyTarget(line, "52 Week Range"));
        while (line.charAt(start) != '>') {
            start--;
        }
        String substring = line.substring(start + 1, getDeci(line, getYearlyTarget(line, "52 Week Range")) + 3);
        return substring;
    }

    private int getYearlyTarget(String line, String target) {
        return line.indexOf(target);
    }

    private int getDeci(String line, int target) {
        return line.indexOf(".", target);
    }

    private String parseMarketCap(String line) {
        int deci;
        int target;
        int start;
        //------------------------------------------
        // Find the Market Cap
        target = getYearlyTarget(line, "Market Cap");
        deci = getDeci(line, target);
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
        target = getYearlyTarget(line, "P/E Ratio");
        deci = getDeci(line, target);
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
        target = getYearlyTarget(line, "EPS");
        deci = getDeci(line, target);
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
        target = getYearlyTarget(line, "Float Shorted");
        deci = getDeci(line, target);
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
        target = getYearlyTarget(line, "Average Volume");
        deci = getDeci(line, target);
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

    private String parseTodaysVolume(String todaysVolumeHtml) {
        int target = getYearlyTarget(todaysVolumeHtml, "Volume");
        int deci = getDeci(todaysVolumeHtml, target);
        int start = deci;
        while (todaysVolumeHtml.charAt(start) != '>') {
            start--;
        }
        return todaysVolumeHtml.substring(start + 1, deci + 4);
    }

    private class CloseValueSums {
        public double sum50 = 0;
        public double sum100 = 0;
    }

    private double[] getCloseValues(String symbol) throws IOException {
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
        while (input.hasNextLine()) {
            String aLine = input.nextLine();
            int aTarget = aLine.lastIndexOf(",");
            // move target to the decimal point
            int aDeci = aTarget - 5;
            int aStart = aDeci;
            while (aLine.charAt(aStart) != ',') {
                aStart--;
            }
            String aClose = aLine.substring(aStart + 1, aDeci + 3);
            closeValues[x] = Double.parseDouble(aClose);
            x++;
        }
        input.close();
        return closeValues;
    }

    private CloseValueSums calculateCloseValueSums(double[] closeValues) {
        CloseValueSums closeValueSums = new CloseValueSums();

        int count = 0;
        for (double closeValue : closeValues) {
            if (count < 50) {
                closeValueSums.sum50 += closeValue;
            } else {
                closeValueSums.sum100 += closeValue;
            }
            count++;
        }
        return closeValueSums;
    }

    private String parseFiftyDayMovingAverage(CloseValueSums closeValueSums) {
        double fiftyAvg = closeValueSums.sum50 / 50;
        // trim to 2 decimal places
        String temp = NumberFormat.getCurrencyInstance().format(fiftyAvg);
        temp = temp.substring(1); // trim off $
        return temp;
    }

    private String parseOneHundredDayMovingAverage(double[] closeValues, CloseValueSums closeValueSums) {
        // trim to 2 decimal places
        closeValueSums.sum100 += closeValueSums.sum50;
        double hundredAvg = closeValueSums.sum100 / closeValues.length;
        String temp2;
        // trim to 2 decimal places
        temp2 = NumberFormat.getCurrencyInstance().format(hundredAvg);
        temp2 = temp2.substring(1); // trim off $
        return temp2;
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
