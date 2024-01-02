package tech.pathtoprogramming.diamanteinvestments;

import org.approvaltests.awt.AwtApprovals;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;

import static java.time.ZoneOffset.UTC;

public class CandleStickTest {


    @Test
    public void candleSticks() {
        Deque<Date> dates = new ArrayDeque<>();
        dates.push(new Date(LocalDateTime.of(2024, 1, 2, 1, 1, 1)
                .toInstant(UTC).toEpochMilli()));
        ArrayDeque<Double> opens = new ArrayDeque<>();
        opens.push(50.34);
        ArrayDeque<Double> highs = new ArrayDeque<>();
        highs.push(71.21);
        ArrayDeque<Double> lows = new ArrayDeque<>();
        lows.push(28.97);
        ArrayDeque<Double> closes = new ArrayDeque<>();
        closes.push(70.67);
        ArrayDeque<Double> volumes = new ArrayDeque<>();
        volumes.push(10_000.0);
        CandleStick candleStick = new CandleStick("AAPL",
                () -> new StockChartData(
                        TimeFrame.INTRADAY,
                        "AAPL",
                        Interval.FIVE,
                        dates,
                        opens,
                        highs,
                        lows,
                        closes,
                        volumes
                ));
        candleStick.setVisible(true);

        AwtApprovals.verify(candleStick);
    }
}