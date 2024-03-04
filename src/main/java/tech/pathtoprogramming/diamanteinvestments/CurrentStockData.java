package tech.pathtoprogramming.diamanteinvestments;

class CurrentStockData {

    private final String stockNameHtml;
    private final String priceHtml;
    private final String changeInDollarsHtml;
    private final String changePercentageHtml;
    private final String closeHtml;
    private final String volumeHtml;

    public CurrentStockData(String stockNameHtml, String priceHtml, String changeInDollarsHtml,
                            String changePercentageHtml, String closeHtml, String volumeHtml) {
        this.stockNameHtml = stockNameHtml;
        this.priceHtml = priceHtml;
        this.changeInDollarsHtml = changeInDollarsHtml;
        this.changePercentageHtml = changePercentageHtml;
        this.closeHtml = closeHtml;
        this.volumeHtml = volumeHtml;
    }

    public String parseStockName() {
        if (stockNameHtml.isEmpty()) {
            throw new IllegalArgumentException("The stock name was not found.");
        }
        int target = stockNameHtml.indexOf("name");
        int deci = stockNameHtml.indexOf(">", target);
        int end = stockNameHtml.indexOf("<", deci);
        return stockNameHtml.substring(deci + 1, end);
    }

    public String parsePrice() {
        int target = priceHtml.indexOf("lastsale");
        int deci = priceHtml.indexOf(".", target);
        int start = deci;
        while (priceHtml.charAt(start) != '>') {
            start--;
        }
        return priceHtml.substring(start + 1, deci + 3);
    }

    public String parseChangeInDollars() {
        int target = changeInDollarsHtml.indexOf("after");
        int deci = changeInDollarsHtml.indexOf(".", target);
        int start = deci;
        while (changeInDollarsHtml.charAt(start) != '>') {
            start--;
        }
        return changeInDollarsHtml.substring(start + 1, deci + 3).trim();
    }

    public String parseChangePercentage() {
        int target = changePercentageHtml.indexOf("after");
        int deci = changePercentageHtml.indexOf(".", target);
        int start = deci;
        while (changePercentageHtml.charAt(start) != '>') {
            start--;
        }
        return changePercentageHtml.substring(start + 1, deci + 3).trim();
    }

    public String parseClose() {
        int target = closeHtml.indexOf("semi");
        int deci = closeHtml.indexOf(".", target);
        int start = deci;
        while (closeHtml.charAt(start) != '$') {
            start--;
        }
        return closeHtml.substring(start + 1, deci + 3);
    }

    public String parseVolume() {
        String line = volumeHtml;
        int target = line.indexOf("Volume");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        // increased length to get the B, M, or K
        return line.substring(start + 1, deci + 4);
    }
}
