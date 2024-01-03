package tech.pathtoprogramming.diamanteinvestments;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StockChartDataTest {

    @Test
    public void readLine() {
        StockChartData stockChartData = new StockChartData();

        String timeStamp = "1991-08-31";
        String open = "187.1500";
        String high = "188.4400";
        String low = "183.8850";
        String close = "185.6400";
        String volume = "82488674";
        stockChartData.readLine(timeStamp + "," + open + "," + high + "," + low + "," + close + "," + volume);

        assertThat(stockChartData.getDates().peek().getYear()).isEqualTo(1991);
    }
}