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
    private String volume = "not found";
    private String averageVolume = "not found";
    private String fiftyDayMA = "not found";
    private String hundredDayMA = "not found";
    private URL iconUrl;

    class CloseValueSums {
        public double sum50 = 0;
        public double sum100 = 0;
    }

    public StockSymbol(String symbol) {
        try {
            Document doc = Jsoup.connect("https://www.marketwatch.com/investing/stock/" + symbol).get();
            String stockNameHtml = doc.select("h1.company__name").toString();
            String priceHtml = doc.select("div.intraday__data").toString();
            String changeInDollarsHtml = doc.select("span.change--point--q").toString();
            String changePercentHtml = doc.select("span.change--percent--q").toString();
            String closeHtml = doc.select("tbody.remove-last-border").toString();
            String volumeHtml = doc.select("div.range__header").toString();
            currentStockData = new CurrentStockData(stockNameHtml, priceHtml, changeInDollarsHtml, changePercentHtml, closeHtml, volumeHtml);

            String line = doc.select("li.kv__item").toString();
            open = parseOpen(line);
            low = parseLow(line);
            high = parseHigh(line);
            yearlyLow = parseYearlyLow(line);
            yearlyHigh = parseYearlyHigh(line);
            marketCap = parseMarketCap(line);
            peRatio = parsePERatio(line);
            eps = parseEarningPerShare(line);
            floatShorted = parsePercentageFloatShorted(line);
            averageVolume = parseAverageVolume(line);


            double[] closeValues = callApiAndParseCloseValues(symbol);
            CloseValueSums closeValueSums = getCloseValueSums(closeValues);

            fiftyDayMA = parseFiftyDayMovingAverage(closeValueSums);
            hundredDayMA = parseHundredDayMovingAverage(closeValues, closeValueSums);

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
        int start = getIndexOfDecimal(line, getIndexOfDayRange(line));
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, getIndexOfDecimal(line, getIndexOfDayRange(line)) + 3);
    }

    private String parseHigh(String line) {
        int index = getIndexOfDecimal(line, getIndexOfDayRange(line)) + 1;
        index = getIndexOfDecimal(line, index);
        int start2 = index;
        while (line.charAt(start2) != '-') {
            start2--;
        }
        return line.substring(start2 + 1, index + 3);
    }

    private int getIndexOfDayRange(String line) {
        return line.indexOf("Day Range");
    }

    private String parseYearlyLow(String line) {
        int start = getIndexOfDecimal(line, getIndexOf52WeekRange(line));
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, getIndexOfDecimal(line, getIndexOf52WeekRange(line)) + 3);
    }

    private String parseYearlyHigh(String line) {
        int index = getIndexOfDecimal(line, getIndexOf52WeekRange(line)) + 1;
        index = getIndexOfDecimal(line, index);
        int start2 = index;
        while (line.charAt(start2) != '-') {
            start2--;
        }
        return line.substring(start2 + 1, index + 3);
    }

    private int getIndexOf52WeekRange(String line) {
        return line.indexOf("52 Week Range");
    }

    private int getIndexOfDecimal(String line, int target) {
        return line.indexOf(".", target);
    }

    private String parseMarketCap(String line) {
        int target = line.indexOf("Market Cap");
        int deci = getIndexOfDecimal(line, target);
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
        int deci = getIndexOfDecimal(line, target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        return (deci - target) < 70 ?
                line.substring(start + 1, deci + 3) :
                "N/A";
    }

    private String parseEarningPerShare(String line) {
        int target = line.indexOf("EPS");
        int deci = getIndexOfDecimal(line, target);
        int start = deci;
        while (line.charAt(start) != '$') {
            start--;
        }
        return (deci - target) < 70 ?
                line.substring(start + 1, deci + 3) :
                "N/A";
    }

    private String parsePercentageFloatShorted(String line) {
        int target = line.indexOf("Float Shorted");
        int deci = getIndexOfDecimal(line, target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        return (deci - target) < 70 ?
                line.substring(start + 1, deci + 3) :
                "N/A";
    }

    private String parseAverageVolume(String line) {
        int target = line.indexOf("Average Volume");
        int deci = getIndexOfDecimal(line, target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        // increased length to get the B, M, or K
        String avgVolume = line.substring(start + 1, deci + 4);
        return avgVolume.charAt(avgVolume.length() - 1) == '<' ?
                avgVolume.substring(0, avgVolume.length() - 1) :
                avgVolume;
    }

    protected double[] callApiAndParseCloseValues(String symbol) throws IOException {
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

    private String trimToTwoDecimalPlaces(double fiftyAvg) {
        return NumberFormat.getCurrencyInstance()
                .format(fiftyAvg)
                .substring(1);
    }

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
        return currentStockData.parseChangePercent();
    }

    public String getClose() {
        return currentStockData.parseClose();
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
        return currentStockData.parseVolume();
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
