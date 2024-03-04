package tech.pathtoprogramming.diamanteinvestments;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.Scanner;

public class StockSymbol {

    private CurrentStockData currentStockData;

    private String open = "not found";
    private String low = "not found";
    private String high = "not found";
    private String yearlyLow = "not found";
    private String yearlyHigh = "not found";
    private String marketCap = "N/A";
    private String peRatio = "N/A";
    private String eps = "N/A";
    private String floatShorted = "N/A";
    private String averageVolume = "not found";
    private String fiftyDayMA = "not found";
    private String hundredDayMA = "not found";
    private URL iconUrl;

    public StockSymbol(String symbol, String baseUrl, String alphaBaseUrl) {
        try {
            Document doc = Jsoup.connect(baseUrl + "/investing/stock/" + symbol).get();

            String stockNameHtml = doc.select("h1.company__name").toString();
            String priceHtml = doc.select("div.intraday__data").toString();
            String changeInDollarsHtml = doc.select("span.change--point--q").toString();
            String changePercentageHtml = doc.select("span.change--percent--q").toString();
            String closeHtml = doc.select("tbody.remove-last-border").toString();
            String volumeHtml = doc.select("div.range__header").toString();
            currentStockData = new CurrentStockData(stockNameHtml, priceHtml, changeInDollarsHtml, changePercentageHtml, closeHtml, volumeHtml);

            String line = doc.select("li.kv__item").toString();
            open = parseOpen(line);
            low = parseLow(line);
            high = parseHigh(line);
            yearlyLow = parseYearlyLow(line);
            yearlyHigh = parseYearlyHigh(line);
            marketCap = parseMarketCap(line);
            peRatio = parsePERatio(line);
            eps = parseEarningPerShare(line);
            floatShorted = parseFloatShortedPercentage(line);
            averageVolume = parseAverageVolume(line);

            double[] closeValues = callApiAndParseCloseValues(symbol, alphaBaseUrl);
            CloseValueSums closeValueSums = getCloseValueSums(closeValues);
            fiftyDayMA = parseFiftyDayMovingAverage(closeValueSums);
            hundredDayMA = parseHundredDayMovingAverage(closeValueSums, closeValues);

            iconUrl = new URL("https://www.google.com/webhp");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String parseOpen(String line) {
        int deci = line.indexOf(".");
        int start = deci;
        while (line.charAt(start) != '$') {
            start--;
        }
        return line.substring(start + 1, deci + 3);
    }

    private String parseLow(String line) {
        int start = line.indexOf(".", indexOfDayRange(line));
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, line.indexOf(".", indexOfDayRange(line)) + 3);
    }

    private String parseHigh(String line) {
        int index = line.indexOf(".", indexOfDayRange(line)) + 1;
        index = line.indexOf(".", index);
        int start = index;
        while (line.charAt(start) != '-') {
            start--;
        }
        return line.substring(start + 1, index + 3);
    }

    private int indexOfDayRange(String line) {
        return line.indexOf("Day Range");
    }

    private String parseYearlyHigh(String line) {
        // Find the decimal of the high
        int index = line.indexOf(".", indexOf52WeekRange(line)) + 1;
        index = line.indexOf(".", index);
        int start2 = index;
        while (line.charAt(start2) != '-') {
            start2--;
        }
        return line.substring(start2 + 1, index + 3);
    }

    private String parseYearlyLow(String line) {
        int start = line.indexOf(".", indexOf52WeekRange(line));
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, line.indexOf(".", indexOf52WeekRange(line)) + 3);
    }

    private int indexOf52WeekRange(String line) {
        return line.indexOf("52 Week Range");
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
        if (deci - target < 70) {
            return line.substring(start + 1, deci + 3);
        }
        return "N/A";
    }

    private String parseEarningPerShare(String line) {
        int target = line.indexOf("EPS");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '$') {
            start--;
        }
        if (deci - target < 70) {
            return line.substring(start + 1, deci + 3);
        }
        return "N/A";
    }

    private String parseFloatShortedPercentage(String line) {
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
        // increased length to get the B, M, or K
        String temp = line.substring(start + 1, deci + 4);
        if (temp.charAt(temp.length() - 1) == '<') {
            return temp.substring(0, temp.length() - 1);
        }
        return temp;
    }

    class CloseValueSums {
        public double sum50 = 0;
        public double sum100 = 0;
    }

    private double[] callApiAndParseCloseValues(String symbol, String alphaBaseUrl) throws IOException {
        String alphaUrl = alphaBaseUrl + "/query?function=TIME_SERIES_DAILY"
                + "&symbol=" + symbol
                + "&apikey=NKNKJCBRLYI9H5SO" +
                "&datatype=csv";
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
            closeValues[x] = Double.parseDouble(aClose);
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
        return NumberFormat.getCurrencyInstance()
                .format(fiftyAvg)
                .substring(1);
    }

    private String parseHundredDayMovingAverage(CloseValueSums closeValueSums, double[] closeValues) {
        closeValueSums.sum100 += closeValueSums.sum50;
        double hundredAvg = closeValueSums.sum100 / closeValues.length;
        return NumberFormat.getCurrencyInstance()
                .format(hundredAvg)
                .substring(1);
    }

    // Getter methods
    public String getStockName() {
        return currentStockData.parseStockName();
    }

    public String getPrice() {
        return currentStockData.parsePrice();
    }

    public String getChange() {
        return currentStockData.parseChangeInDollars();
    }

    public String getChangePercent() {
        return currentStockData.parseChangePercentage();
    }

    public String getClose() {
        return currentStockData.parseClose();
    }

    public String getVolume() {
        return currentStockData.parseVolume();
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
