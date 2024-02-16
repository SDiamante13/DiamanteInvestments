package tech.pathtoprogramming.diamanteinvestments;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.Objects;
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

            stockName = parseStockName(doc.select("h1.company__name").toString());
            price = parsePrice(doc.select("div.intraday__data").toString());
            change = parseChangeInDollars(doc.select("span.change--point--q").toString());
            changePercent = parseChangePercent(doc.select("span.change--percent--q").toString());
            close = parseClose(doc.select("tbody.remove-last-border").toString());

            String line = doc.select("li.kv__item").toString();
            open = parseOpen(line);
            parseLowAndHighAndAssignInstanceVariables(line);
            yearlyLow = parseYearlyLow(line);
            yearlyHigh = parseYearlyHigh(line);
            marketCap = parseMarketCap(line);
            peRatio = parsePERatio(line);
            eps = parseEarningPerShare(line);
            floatShorted = parsePercentageFloatShorted(line);
            averageVolume = parseAverageVolume(line);

            volume = parseVolume(doc.select("div.range__header").toString());

            callAlphaAdvantageAPIAndSet50DayAnd100DayMovingAverages(symbol);

            iconUrl = new URL("https://www.google.com/webhp");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String parseStockName(String stockNameHtml) {
        int target = stockNameHtml.indexOf("name");
        int deci = stockNameHtml.indexOf(">", target);
        int end = stockNameHtml.indexOf("<", deci);
        if (stockNameHtml.isEmpty()) {
            throw new IllegalArgumentException("The stock symbol is invalid");
        }
        return stockNameHtml.substring(deci + 1, end);
    }

    private String parsePrice(String priceHtml) {
        int target = priceHtml.indexOf("lastsale");
        int deci = getIndexOfDecimal(priceHtml, target);
        int start = deci;
        while (priceHtml.charAt(start) != '>') {
            start--;
        }
        return priceHtml.substring(start + 1, deci + 3);
    }

    private String parseChangeInDollars(String changeInDollarsHtml) {
        int target = changeInDollarsHtml.indexOf("after");
        int deci = getIndexOfDecimal(changeInDollarsHtml, target);
        int start = deci;
        while (changeInDollarsHtml.charAt(start) != '>') {
            start--;
        }
        return changeInDollarsHtml.substring(start + 1, deci + 3).trim();
    }

    private String parseChangePercent(String changePercentHtml) {
        int target = changePercentHtml.indexOf("after");
        int deci = getIndexOfDecimal(changePercentHtml, target);
        int start = deci;
        while (changePercentHtml.charAt(start) != '>') {
            start--;
        }
        return changePercentHtml.substring(start + 1, deci + 3).trim();
    }

    private String parseClose(String closeHtml) {
        int target = closeHtml.indexOf("semi");
        int deci = getIndexOfDecimal(closeHtml, target);
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

    private void parseLowAndHighAndAssignInstanceVariables(String line) {
        int start;
        int target;
        int deci;
        //------------------------------------------
        // Find low and high of the stock
        target = line.indexOf("Day Range");
        deci = getIndexOfDecimal(line, target);
        start = deci;
        int index = deci + 1;
        while (line.charAt(start) != '>') {
            start--;
        }
        low = line.substring(start + 1, deci + 3);
        // Find the decimal of the high
        index = getIndexOfDecimal(line, index);
        int start2 = index;
        while (line.charAt(start2) != '-') {
            start2--;
        }
        high = line.substring(start2 + 1, index + 3);
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

    private String parseVolume(String volumeHtml) {
        int target = volumeHtml.indexOf("Volume");
        int deci = getIndexOfDecimal(volumeHtml, target);
        int start = deci;
        while (volumeHtml.charAt(start) != '>') {
            start--;
        }
        // increased length to get the B, M, or K
        return volumeHtml.substring(start + 1, deci + 4);
    }

    protected void callAlphaAdvantageAPIAndSet50DayAnd100DayMovingAverages(String symbol) throws IOException {
        //-------------------------------------------------------------------------------------
        // Calculate 50 day and 100 day moving averages
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockSymbol that = (StockSymbol) o;

        if (!Objects.equals(stockName, that.stockName)) return false;
        if (!Objects.equals(price, that.price)) return false;
        if (!Objects.equals(change, that.change)) return false;
        if (!Objects.equals(changePercent, that.changePercent))
            return false;
        if (!Objects.equals(close, that.close)) return false;
        if (!Objects.equals(open, that.open)) return false;
        if (!Objects.equals(low, that.low)) return false;
        if (!Objects.equals(high, that.high)) return false;
        if (!Objects.equals(yearlyLow, that.yearlyLow)) return false;
        if (!Objects.equals(yearlyHigh, that.yearlyHigh)) return false;
        if (!Objects.equals(marketCap, that.marketCap)) return false;
        if (!Objects.equals(peRatio, that.peRatio)) return false;
        if (!Objects.equals(eps, that.eps)) return false;
        if (!Objects.equals(floatShorted, that.floatShorted)) return false;
        if (!Objects.equals(volume, that.volume)) return false;
        if (!Objects.equals(averageVolume, that.averageVolume))
            return false;
        if (!Objects.equals(fiftyDayMA, that.fiftyDayMA)) return false;
        if (!Objects.equals(hundredDayMA, that.hundredDayMA)) return false;
        return Objects.equals(iconUrl, that.iconUrl);
    }

    @Override
    public int hashCode() {
        int result = stockName != null ? stockName.hashCode() : 0;
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (change != null ? change.hashCode() : 0);
        result = 31 * result + (changePercent != null ? changePercent.hashCode() : 0);
        result = 31 * result + (close != null ? close.hashCode() : 0);
        result = 31 * result + (open != null ? open.hashCode() : 0);
        result = 31 * result + (low != null ? low.hashCode() : 0);
        result = 31 * result + (high != null ? high.hashCode() : 0);
        result = 31 * result + (yearlyLow != null ? yearlyLow.hashCode() : 0);
        result = 31 * result + (yearlyHigh != null ? yearlyHigh.hashCode() : 0);
        result = 31 * result + (marketCap != null ? marketCap.hashCode() : 0);
        result = 31 * result + (peRatio != null ? peRatio.hashCode() : 0);
        result = 31 * result + (eps != null ? eps.hashCode() : 0);
        result = 31 * result + (floatShorted != null ? floatShorted.hashCode() : 0);
        result = 31 * result + (volume != null ? volume.hashCode() : 0);
        result = 31 * result + (averageVolume != null ? averageVolume.hashCode() : 0);
        result = 31 * result + (fiftyDayMA != null ? fiftyDayMA.hashCode() : 0);
        result = 31 * result + (hundredDayMA != null ? hundredDayMA.hashCode() : 0);
        result = 31 * result + (iconUrl != null ? iconUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StockSymbol{" +
                "stockName='" + stockName + '\'' +
                ", price='" + price + '\'' +
                ", change='" + change + '\'' +
                ", changePercent='" + changePercent + '\'' +
                ", close='" + close + '\'' +
                ", open='" + open + '\'' +
                ", low='" + low + '\'' +
                ", high='" + high + '\'' +
                ", yearlyLow='" + yearlyLow + '\'' +
                ", yearlyHigh='" + yearlyHigh + '\'' +
                ", marketCap='" + marketCap + '\'' +
                ", peRatio='" + peRatio + '\'' +
                ", eps='" + eps + '\'' +
                ", floatShorted='" + floatShorted + '\'' +
                ", volume='" + volume + '\'' +
                ", averageVolume='" + averageVolume + '\'' +
                ", fiftyDayMA='" + fiftyDayMA + '\'' +
                ", hundredDayMA='" + hundredDayMA + '\'' +
                ", iconUrl=" + iconUrl +
                '}';
    }
}
