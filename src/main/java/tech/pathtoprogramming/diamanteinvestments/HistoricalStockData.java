package tech.pathtoprogramming.diamanteinvestments;

class HistoricalStockData {

    private final String html;

    public HistoricalStockData(String html) {
        this.html = html;
    }

    public String parseOpen() {
        int deci = html.indexOf(".");
        int start = deci;
        while (html.charAt(start) != '$') {
            start--;
        }
        return html.substring(start + 1, deci + 3);
    }

    public String parseLow() {
        int start = html.indexOf(".", html.indexOf("Day Range"));
        while (html.charAt(start) != '>') {
            start--;
        }
        return html.substring(start + 1, html.indexOf(".", html.indexOf("Day Range")) + 3);
    }

    public String parseHigh() {
        int index = html.indexOf(".", html.indexOf("Day Range")) + 1;
        index = html.indexOf(".", index);
        int start = index;
        while (html.charAt(start) != '-') {
            start--;
        }
        return html.substring(start + 1, index + 3);
    }

    public String parseYearlyLow() {
        int start = html.indexOf(".", html.indexOf("52 Week Range"));
        while (html.charAt(start) != '>') {
            start--;
        }
        return html.substring(start + 1, html.indexOf(".", html.indexOf("52 Week Range")) + 3);
    }

    public String parseYearlyHigh() {
        // Find the decimal of the high
        int index = html.indexOf(".", html.indexOf("52 Week Range")) + 1;
        index = html.indexOf(".", index);
        int start2 = index;
        while (html.charAt(start2) != '-') {
            start2--;
        }
        return html.substring(start2 + 1, index + 3);
    }

    public String parseMarketCap() {
        int target = html.indexOf("Market Cap");
        int deci = html.indexOf(".", target);
        int start = deci;
        if (deci - target > 200) { // ETFs will show N/A
            return "N/A";
        }
        while (html.charAt(start) != '$') {
            start--;
        }
        String result = html.substring(start + 1, deci + 4); // increased length to get the B, M, or K
        if (result.endsWith("<")) {
            result = result.substring(0, result.length() - 2);
        }
        if (!result.endsWith("B") && !result.endsWith("M") && !result.endsWith("K")) {
            result = "N/A";
        }
        return result;
    }

    public String parsePERatio() {
        int target = html.indexOf("P/E Ratio");
        int deci = html.indexOf(".", target);
        int start = deci;
        while (html.charAt(start) != '>') {
            start--;
        }
        if (deci - target < 70) {
            return html.substring(start + 1, deci + 3);
        }
        return "N/A";
    }

    public String parseEarningPerShare() {
        int target = html.indexOf("EPS");
        int deci = html.indexOf(".", target);
        int start = deci;
        while (html.charAt(start) != '$') {
            start--;
        }
        if (deci - target < 70) {
            return html.substring(start + 1, deci + 3);
        }
        return "N/A";
    }

    public String parseFloatShortedPercentage() {
        int target = html.indexOf("Float Shorted");
        int deci = html.indexOf(".", target);
        int start = deci;
        while (html.charAt(start) != '>') {
            start--;
        }
        if ((deci - target) < 70) {
            return html.substring(start + 1, deci + 3);
        }
        return "N/A";
    }

    public String parseAverageVolume() {
        int target = html.indexOf("Average Volume");
        int deci = html.indexOf(".", target);
        int start = deci;
        while (html.charAt(start) != '>') {
            start--;
        }
        // increased length to get the B, M, or K
        String temp = html.substring(start + 1, deci + 4);
        if (temp.charAt(temp.length() - 1) == '<') {
            return temp.substring(0, temp.length() - 1);
        }
        return temp;
    }
}
