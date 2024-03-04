package tech.pathtoprogramming.diamanteinvestments;

import java.text.NumberFormat;

class MovingAverages {

    private final double[] closeValues;

    public MovingAverages(double[] closeValues) {
        this.closeValues = closeValues;
    }

    public String parseFiftyDayMovingAverage() {
        CloseValueSums closeValueSums = CloseValueSums.create(closeValues);
        return NumberFormat.getCurrencyInstance()
                .format(closeValueSums.calculateFiftyDayMovingAverage())
                .substring(1);
    }

    public String parseHundredDayMovingAverage() {
        CloseValueSums closeValueSums = CloseValueSums.create(closeValues);
        return NumberFormat.getCurrencyInstance()
                .format(closeValueSums.calculateOneHundredDayMovingAverage())
                .substring(1);
    }

}
