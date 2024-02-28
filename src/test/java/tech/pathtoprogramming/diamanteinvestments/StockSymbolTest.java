package tech.pathtoprogramming.diamanteinvestments;

import org.approvaltests.Approvals;
import org.approvaltests.reporters.QuietReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Test;

import java.util.stream.DoubleStream;

import static java.lang.String.format;

@UseReporter(QuietReporter.class)
public class StockSymbolTest {

    /**
     * This test only works when the US stock market is closed
     * When the market is open the prices fluctuate and test is flaky
     */
    @Test
    public void fetchesStockDataFromMarketWatch() {
        StockSymbol stockSymbol = new StockSymbol("NFLX") {
            @Override
            protected double[] callApiAndParseCloseValues(String symbol) {
                return DoubleStream.of(
                                593.46, 579.33, 554.52, 557.85, 561.32, 558.53, 559.3, 555.88, 562.06, 564.64, 567.51,
                                564.11, 562.85, 575.79, 570.42, 562.0, 544.87, 492.19, 485.71, 482.95, 485.31, 480.33,
                                481.24, 492.16, 492.23, 478.33, 482.09, 485.03, 474.06, 474.67, 470.26, 468.5, 486.88,
                                490.51, 491.79, 491.19, 486.76, 491.61, 489.27, 495.02, 486.12, 472.06, 469.83, 479.98,
                                463.0, 459.89, 453.76, 452.0, 446.73, 455.15, 453.9, 465.74, 473.97, 477.19, 479.0,
                                479.17, 479.56, 478.0, 474.95, 474.47, 465.91, 466.95, 461.94, 448.65, 444.62, 447.24,
                                435.15, 436.65, 434.61, 434.74, 432.36, 424.71, 420.19, 411.69, 410.08, 397.87, 403.54,
                                411.25, 413.73, 406.84, 400.96, 401.77, 346.19, 355.72, 360.82, 355.68, 361.2, 365.93,
                                373.32, 385.95, 381.51, 372.59, 376.9, 376.75, 380.33, 377.6, 376.36, 377.59, 379.25,
                                384.8)
                        .toArray();
            }
        };

        Approvals.verify(print(stockSymbol));
    }

    private String print(StockSymbol stockSymbol) {
        return stockSymbol.getStockName() + "\n" +
                "-------------------------------\n" +
                format("Open %40s\n", stockSymbol.getOpen()) +
                format("Close %40s\n", stockSymbol.getClose()) +
                format("Day's Range %40s-%s\n", stockSymbol.getLow(), stockSymbol.getHigh()) +
                format("52 Week Range %40s-%s\n", stockSymbol.getYearlyLow(), stockSymbol.getYearlyHigh()) +
                format("Volume %40s\n", stockSymbol.getVolume()) +
                format("Average Volume %40s\n", stockSymbol.getAverageVolume()) +
                format("Market Cap %40s\n", stockSymbol.getMarketCap()) +
                format("P/E Ratio %40s\n", stockSymbol.getPeRatio()) +
                format("EPS %40s\n", stockSymbol.getEps()) +
                format("Float Shorted %40s\n", stockSymbol.getFloatShorted()) +
                format("50-Day Moving Average %40s\n", stockSymbol.getFiftyDayMA()) +
                format("100-Day Moving Average %40s\n", stockSymbol.getHundredDayMA());
    }
}