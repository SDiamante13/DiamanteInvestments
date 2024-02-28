package tech.pathtoprogramming.diamanteinvestments;

public class CurrentStockData {

    private final String stockNameHtml;
    private final String priceHtml;
    private final String changeInDollarsHtml;
    private final String changePercentHtml;
    private final String closeHtml;
    private final String volumeHtml;

    public CurrentStockData(String stockNameHtml, String priceHtml, String changeInDollarsHtml,
                            String changePercentHtml, String closeHtml, String volumeHtml) {
        this.stockNameHtml = stockNameHtml;
        this.priceHtml = priceHtml;
        this.changeInDollarsHtml = changeInDollarsHtml;
        this.changePercentHtml = changePercentHtml;
        this.closeHtml = closeHtml;
        this.volumeHtml = volumeHtml;
    }

    String parseStockName() {
        int target = stockNameHtml.indexOf("name");
        int deci = stockNameHtml.indexOf(">", target);
        int end = stockNameHtml.indexOf("<", deci);
        if (stockNameHtml.isEmpty()) {
            throw new IllegalArgumentException("The stock symbol is invalid");
        }
        return stockNameHtml.substring(deci + 1, end);
    }

    String parsePrice() {
        int target = priceHtml.indexOf("lastsale");
        int deci = getIndexOfDecimal(priceHtml, target);
        int start = deci;
        while (priceHtml.charAt(start) != '>') {
            start--;
        }
        return priceHtml.substring(start + 1, deci + 3);
    }

    String parseChangeInDollars() {
        int target = changeInDollarsHtml.indexOf("after");
        int deci = getIndexOfDecimal(changeInDollarsHtml, target);
        int start = deci;
        while (changeInDollarsHtml.charAt(start) != '>') {
            start--;
        }
        return changeInDollarsHtml.substring(start + 1, deci + 3).trim();
    }

    String parseChangePercent() {
        int target = changePercentHtml.indexOf("after");
        int deci = getIndexOfDecimal(changePercentHtml, target);
        int start = deci;
        while (changePercentHtml.charAt(start) != '>') {
            start--;
        }
        return changePercentHtml.substring(start + 1, deci + 3).trim();
    }

    String parseClose() {
        int target = closeHtml.indexOf("semi");
        int deci = getIndexOfDecimal(closeHtml, target);
        int start = deci;
        while (closeHtml.charAt(start) != '$') {
            start--;
        }
        return closeHtml.substring(start + 1, deci + 3);
    }

    String parseVolume() {
        int target = volumeHtml.indexOf("Volume");
        int deci = getIndexOfDecimal(volumeHtml, target);
        int start = deci;
        while (volumeHtml.charAt(start) != '>') {
            start--;
        }
        // increased length to get the B, M, or K
        return volumeHtml.substring(start + 1, deci + 4);
    }

    private int getIndexOfDecimal(String line, int target) {
        return line.indexOf(".", target);
    }
}