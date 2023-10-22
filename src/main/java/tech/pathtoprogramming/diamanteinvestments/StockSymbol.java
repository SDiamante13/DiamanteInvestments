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
            changePercent = parseChangeInPercentage(changeInPercentageHtml);
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
            floatShorted = parsePercentageFloatShorted(line);
            averageVolume = parseAverageVolume(line);

            double[] closeValues = callApi_parseCloseValues(symbol);
            CloseValueSums closeValueSums = getCloseValueSums(closeValues);

            fiftyDayMA = parseFiftyDayMovingAverage(closeValueSums);
            hundredDayMA = parseHundredDayMovingAverage(closeValues, closeValueSums);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String parseStockName(String stockNameHtml) {
        int target = getTarget(stockNameHtml, "name");
        int deci = stockNameHtml.indexOf(">", target);
        int end = stockNameHtml.indexOf("<", deci);
        if (stockNameHtml.isEmpty()) {
            throw new IllegalArgumentException("The stock symbol is invalid");
        }
        return stockNameHtml.substring(deci + 1, end);
    }

    private String parsePrice(String priceHtml) {
        int target = getTarget(priceHtml, "lastsale");
        int deci = priceHtml.indexOf(".", target);
        int start = deci;
        while (priceHtml.charAt(start) != '>') {
            start--;
        }
        return priceHtml.substring(start + 1, deci + 3);
    }

    private String parseChangeInDollars(String changeInDollarsHtml) {
        int target = getTarget(changeInDollarsHtml, "after");
        int deci = changeInDollarsHtml.indexOf(".", target);
        int start = deci;
        while (changeInDollarsHtml.charAt(start) != '>') {
            start--;
        }
        return changeInDollarsHtml.substring(start + 1, deci + 3).trim();
    }

    private String parseChangeInPercentage(String changeInPercentageHtml) {
        int target = getTarget(changeInPercentageHtml, "after");
        int deci = changeInPercentageHtml.indexOf(".", target);
        int start = deci;
        while (changeInPercentageHtml.charAt(start) != '>') {
            start--;
        }
        return changeInPercentageHtml.substring(start + 1, deci + 3).trim();
    }

    private String parseClose(String closeHtml) {
        int target = getTarget(closeHtml, "semi");
        int deci = closeHtml.indexOf(".", target);
        int start = deci;
        while (closeHtml.charAt(start) != '$') {
            start--;
        }
        return closeHtml.substring(start + 1, deci + 3);
    }

    private String parseOpen(String line) {
        int deci = getTarget(line, ".");
        int start = deci;
        while (line.charAt(start) != '$') {
            start--;
        }
        return line.substring(start + 1, deci + 3);
    }

    private String parseLow(String line) {
        int start = getDeci(line, getTarget(line, "Day Range"));
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, getDeci(line, getTarget(line, "Day Range")) + 3);
    }

    private String parseHigh(String line) {
        int index = getDeci(line, getTarget(line, "Day Range")) + 1;
        index = getDeci(line, index);
        int start2 = index;
        while (line.charAt(start2) != '-') {
            start2--;
        }
        return line.substring(start2 + 1, index + 3);
    }

    private String parseYearlyLow(String line) {
        int start = getDeci(line, getTarget(line, "52 Week Range"));
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, getDeci(line, getTarget(line, "52 Week Range")) + 3);
    }

    private String parseYearlyHigh(String line) {
        int index = getDeci(line, getTarget(line, "52 Week Range")) + 1;
        // Find the decimal of the high
        index = getDeci(line, index);
        int start2 = index;
        while (line.charAt(start2) != '-') {
            start2--;
        }
        return line.substring(start2 + 1, index + 3);
    }

    private int getTarget(String line, String title) {
        return line.indexOf(title);
    }

    private int getDeci(String line, int target) {
        return line.indexOf(".", target);
    }

    private String parseMarketCap(String line) {
        int target = getTarget(line, "Market Cap");
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
        int target = getTarget(line, "P/E Ratio");
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
        int target = getTarget(line, "EPS");
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
        int target = getTarget(line, "Float Shorted");
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
        int target = getTarget(line, "Average Volume");
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

    private String parseTodaysVolume(String todaysVolumeHtml) {
        int target = getTarget(todaysVolumeHtml, "Volume");
        int deci = todaysVolumeHtml.indexOf(".", target);
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

    private double[] callApi_parseCloseValues(String symbol) throws IOException {
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
            closeValues[x] = Double.parseDouble(aClose); // convert String to int and store into array
            x++;
        }
        input.close();
        return closeValues;
    }

    private CloseValueSums getCloseValueSums(double[] closeValues) {
        CloseValueSums closeValueSums = new CloseValueSums();
        int count = 0;
        for (double cV : closeValues) {
            if (count < 50) {
                closeValueSums.sum50 += cV;
            } else {
                closeValueSums.sum100 += cV;
            }
            count++;
        }
        return closeValueSums;
    }

    private String parseFiftyDayMovingAverage(CloseValueSums closeValueSums) {
        double fiftyAvg = closeValueSums.sum50 / 50;
        return trimToTwoDecimalPlaces(fiftyAvg);
    }

    private String parseHundredDayMovingAverage(double[] closeValues, CloseValueSums closeValueSums) {
        closeValueSums.sum100 += closeValueSums.sum50;
        double hundredAvg = closeValueSums.sum100 / closeValues.length;
        return trimToTwoDecimalPlaces(hundredAvg);
    }

    private String trimToTwoDecimalPlaces(double hundredAvg) {
        return NumberFormat.getCurrencyInstance()
                .format(hundredAvg)
                .substring(1);
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
