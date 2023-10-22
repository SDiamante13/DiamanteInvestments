package tech.pathtoprogramming.diamanteinvestments;

class CurrentStockData {

    private final String stockNameHtml;
    private final String priceHtml;
    private final String changeInDollarsHtml;
    private final String changeInPercentageHtml;
    private final String closeHtml;
    private final String todaysVolumeHtml;

    public CurrentStockData(String stockNameHtml, String priceHtml, String changeInDollarsHtml, String changeInPercentageHtml, String closeHtml, String todaysVolumeHtml) {
        this.stockNameHtml = stockNameHtml;
        this.priceHtml = priceHtml;
        this.changeInDollarsHtml = changeInDollarsHtml;
        this.changeInPercentageHtml = changeInPercentageHtml;
        this.closeHtml = closeHtml;
        this.todaysVolumeHtml = todaysVolumeHtml;
    }

    public String parseStockName() {
        int target = getTarget(stockNameHtml, "name");
        int deci = stockNameHtml.indexOf(">", target);
        int end = stockNameHtml.indexOf("<", deci);
        if (stockNameHtml.isEmpty()) {
            throw new IllegalArgumentException("The stock symbol is invalid");
        }
        return stockNameHtml.substring(deci + 1, end);
    }

    public String parsePrice() {
        int target = getTarget(priceHtml, "lastsale");
        int deci = priceHtml.indexOf(".", target);
        int start = deci;
        while (priceHtml.charAt(start) != '>') {
            start--;
        }
        return priceHtml.substring(start + 1, deci + 3);
    }

    public String parseChangeInDollars() {
        int target = getTargetDependingOnTag();
        int deci = changeInDollarsHtml.indexOf(".", target);
        int start = deci;
        while (changeInDollarsHtml.charAt(start) != '>') {
            start--;
        }
        return changeInDollarsHtml.substring(start + 1, deci + 3).trim();
    }

    private int getTargetDependingOnTag() {
        boolean isDuringTradingHours = getTarget(changeInDollarsHtml, "after") == -1;
        if (isDuringTradingHours) {
            return getTarget(changeInDollarsHtml, "composite");
        }
        return getTarget(changeInDollarsHtml, "after");
    }

    public String parseChangeInPercentage() {
        int target = getTarget(changeInPercentageHtml, "after");
        int deci = changeInPercentageHtml.indexOf(".", target);
        int start = deci;
        while (changeInPercentageHtml.charAt(start) != '>') {
            start--;
        }
        return changeInPercentageHtml.substring(start + 1, deci + 3).trim();
    }

    public String parseClose() {
        int target = getTarget(closeHtml, "semi");
        int deci = closeHtml.indexOf(".", target);
        int start = deci;
        while (closeHtml.charAt(start) != '$') {
            start--;
        }
        return closeHtml.substring(start + 1, deci + 3);
    }

    public String parseTodaysVolume() {
        int target = getTarget(todaysVolumeHtml, "Volume");
        int deci = todaysVolumeHtml.indexOf(".", target);
        int start = deci;
        while (todaysVolumeHtml.charAt(start) != '>') {
            start--;
        }
        return todaysVolumeHtml.substring(start + 1, deci + 4);
    }

    private int getTarget(String line, String title) {
        return line.indexOf(title);
    }
}
