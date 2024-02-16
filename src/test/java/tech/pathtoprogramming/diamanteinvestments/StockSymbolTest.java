package tech.pathtoprogramming.diamanteinvestments;

import org.approvaltests.Approvals;
import org.approvaltests.reporters.QuietReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Test;

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
            protected void callAlphaAdvantageAPIAndSet50DayAnd100DayMovingAverages(String symbol) {
            }
        };

        Approvals.verify(stockSymbol);
    }
}