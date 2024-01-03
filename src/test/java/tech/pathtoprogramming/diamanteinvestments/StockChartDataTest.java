package tech.pathtoprogramming.diamanteinvestments;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

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

        stockChartData.readLine(timeStamp + "," + open + "," + high +
                "," + low + "," + close + "," + volume);

        assertThat(stockChartData.toString())
                .isEqualTo("StockChartData{" +
                        "dates=[Thu Oct 01 00:00:00 EDT 3891], " +
                        "opens=[187.15], " +
                        "highs=[188.44], " +
                        "lows=[183.88], " +
                        "closes=[185.64], " +
                        "volumes=[8248867.0], " +
                        "timeSeries=TIME_SERIES_DAILY, " +
                        "interval=60min, " +
                        "date=Thu Oct 01 00:00:00 EDT 3891}");
    }
}