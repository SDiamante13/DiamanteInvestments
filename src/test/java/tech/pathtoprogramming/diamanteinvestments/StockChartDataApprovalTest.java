package tech.pathtoprogramming.diamanteinvestments;

import org.approvaltests.Approvals;
import org.junit.Test;

public class StockChartDataApprovalTest {

    @Test
    public void retrievesStockChartData() {
        StockChartData stockChartData = new StockChartData(TimeFrame.DAILY, "AAPL", Interval.SIXTY);

        Approvals.verify(stockChartData);
    }
}