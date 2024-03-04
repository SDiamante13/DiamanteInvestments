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

    public StockSymbol(String symbol, String baseUrl, String alphaBaseUrl) {
        try {
            Document doc = Jsoup.connect(baseUrl + "/investing/stock/" + symbol).get();

            //------------------------------------------
            // Find the stock name
            // h1> class = company__name
            // use div# for id
            Elements ele = doc.select("h1.company__name");
            String line = ele.toString();
            int target = line.indexOf("name");
            int deci = line.indexOf(">", target);
            int end = line.indexOf("<", deci);
            if (line.isEmpty()) {
                return;
            }
            stockName = line.substring(deci + 1, end);

            price = parsePrice(doc.select("div.intraday__data").toString());
            change = parseChangeInDollars(doc.select("span.change--point--q").toString());
            changePercent = parseChangePercentage(doc.select("span.change--percent--q").toString());
            close = parseClose(doc.select("tbody.remove-last-border").toString());
            volume = parseVolume(doc.select("div.range__header").toString());

            line = doc.select("li.kv__item").toString();
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

    private String parsePrice(String priceHtml) {
        int target = priceHtml.indexOf("lastsale");
        int deci = indexOfDecimal(priceHtml, target);
        int start = deci;
        while (priceHtml.charAt(start) != '>') {
            start--;
        }
        return priceHtml.substring(start + 1, deci + 3);
    }

    private String parseChangeInDollars(String changeInDollarsHtml) {
        int target = changeInDollarsHtml.indexOf("after");
        int deci = indexOfDecimal(changeInDollarsHtml, target);
        int start = deci;
        while (changeInDollarsHtml.charAt(start) != '>') {
            start--;
        }
        return changeInDollarsHtml.substring(start + 1, deci + 3).trim();
    }

    private String parseChangePercentage(String changePercentageHtml) {
        int target = changePercentageHtml.indexOf("after");
        int deci = indexOfDecimal(changePercentageHtml, target);
        int start = deci;
        while (changePercentageHtml.charAt(start) != '>') {
            start--;
        }
        return changePercentageHtml.substring(start + 1, deci + 3).trim();
    }

    private String parseClose(String closeHtml) {
        int target = closeHtml.indexOf("semi");
        int deci = indexOfDecimal(closeHtml, target);
        int start = deci;
        while (closeHtml.charAt(start) != '$') {
            start--;
        }
        return closeHtml.substring(start + 1, deci + 3);
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
        int start = indexOfDecimal(line, indexOfDayRange(line));
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, indexOfDecimal(line, indexOfDayRange(line)) + 3);
    }

    private String parseHigh(String line) {
        int index = indexOfDecimal(line, indexOfDayRange(line)) + 1;
        index = indexOfDecimal(line, index);
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
        int index = indexOfDecimal(line, indexOf52WeekRange(line)) + 1;
        index = indexOfDecimal(line, index);
        int start2 = index;
        while (line.charAt(start2) != '-') {
            start2--;
        }
        return line.substring(start2 + 1, index + 3);
    }

    private String parseYearlyLow(String line) {
        int start = indexOfDecimal(line, indexOf52WeekRange(line));
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, indexOfDecimal(line, indexOf52WeekRange(line)) + 3);
    }

    private int indexOf52WeekRange(String line) {
        return line.indexOf("52 Week Range");
    }

    private int indexOfDecimal(String line, int target) {
        return line.indexOf(".", target);
    }

    private String parseMarketCap(String line) {
        int target = line.indexOf("Market Cap");
        int deci = indexOfDecimal(line, target);
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
        int deci = indexOfDecimal(line, target);
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
        int deci = indexOfDecimal(line, target);
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
        int deci = indexOfDecimal(line, target);
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
        int deci = indexOfDecimal(line, target);
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

    private String parseVolume(String volumeHtml) {
        String line = volumeHtml;
        int target = line.indexOf("Volume");
        int deci = indexOfDecimal(line, target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        // increased length to get the B, M, or K
        return line.substring(start + 1, deci + 4);
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
