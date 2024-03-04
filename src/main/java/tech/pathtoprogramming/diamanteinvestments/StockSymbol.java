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
    private HistoricalStockData historicalStockData;
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
            historicalStockData = new HistoricalStockData(doc.select("li.kv__item").toString());


            double[] closeValues = callApiAndParseCloseValues(symbol, alphaBaseUrl);
            CloseValueSums closeValueSums = getCloseValueSums(closeValues);
            fiftyDayMA = parseFiftyDayMovingAverage(closeValueSums);
            hundredDayMA = parseHundredDayMovingAverage(closeValueSums, closeValues);

            iconUrl = new URL("https://www.google.com/webhp");
        } catch (Exception e) {
            System.out.println(e);
        }
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
        return historicalStockData.parseOpen();
    }

    public String getLow() {
        return historicalStockData.parseLow();
    }

    public String getHigh() {
        return historicalStockData.parseHigh();
    }

    public String getYearlyLow() {
        return historicalStockData.parseYearlyLow();
    }

    public String getYearlyHigh() {
        return historicalStockData.parseYearlyHigh();
    }

    public String getMarketCap() {
        return historicalStockData.parseMarketCap();
    }

    public String getPeRatio() {
        return historicalStockData.parsePERatio();
    }

    public String getEps() {
        return historicalStockData.parseEarningPerShare();
    }

    public String getFloatShorted() {
        return historicalStockData.parseFloatShortedPercentage();
    }

    public String getAverageVolume() {
        return historicalStockData.parseAverageVolume();
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
