package tech.pathtoprogramming.diamanteinvestments;

class CloseValueSums {

    private final double lastFiftyClosingTotal;
    private final double lastOneHundredClosingTotal;

    public static CloseValueSums create(double[] closeValues) {
        double sum50 = 0;
        double sum100 = 0;
        for (int i = 0; i < closeValues.length; i++) {
            double closeValue = closeValues[i];
            if (i < 50) {
                sum50 += closeValue;
            }
            sum100 += closeValue;
        }
        return new CloseValueSums(sum50, sum100);
    }

    private CloseValueSums(double lastFiftyClosingTotal, double lastOneHundredClosingTotal) {
        this.lastFiftyClosingTotal = lastFiftyClosingTotal;
        this.lastOneHundredClosingTotal = lastOneHundredClosingTotal;
    }

    public double calculateFiftyDayMovingAverage() {
        return lastFiftyClosingTotal / 50;
    }

    public double calculateOneHundredDayMovingAverage() {
        return lastOneHundredClosingTotal / 100;
    }
}
