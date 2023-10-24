package tech.pathtoprogramming.diamanteinvestments;

class HistoricalStockData {

    private final String line;

    public HistoricalStockData(String line) {
        this.line = line;
    }

    public String parseOpen() {
        int deci = getTarget(line, ".");
        int start = deci;
        while (line.charAt(start) != '$') {
            start--;
        }
        return line.substring(start + 1, deci + 3);
    }

    public String parseLow() {
        int start = getDeci(line, getTarget(line, "Day Range"));
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, getDeci(line, getTarget(line, "Day Range")) + 3);
    }

    public String parseHigh() {
        int index = getDeci(line, getTarget(line, "Day Range")) + 1;
        index = getDeci(line, index);
        int start2 = index;
        while (line.charAt(start2) != '-') {
            start2--;
        }
        return line.substring(start2 + 1, index + 3);
    }

    public String parseYearlyLow() {
        int start = getDeci(line, getTarget(line, "52 Week Range"));
        while (line.charAt(start) != '>') {
            start--;
        }
        return line.substring(start + 1, getDeci(line, getTarget(line, "52 Week Range")) + 3);
    }

    public String parseYearlyHigh() {
        int index = getDeci(line, getTarget(line, "52 Week Range")) + 1;
        // Find the decimal of the high
        index = getDeci(line, index);
        int start2 = index;
        while (line.charAt(start2) != '-') {
            start2--;
        }
        return line.substring(start2 + 1, index + 3);
    }

    public String parseMarketCap() {
        int target = getTarget(line, "Market Cap");
        int deci = line.indexOf(".", target);
        int start = deci;
        if (deci - target > 200) { // ETFs will show N/A
            return "N/A";
        }
        while (line.charAt(start) != '$') {
            start--;
        }
        String result = line.substring(start + 1, deci + 4); // increased length to get the B, M, or K
        if (result.endsWith("<")) {
            result = result.substring(0, result.length() - 2);
        }
        if (!result.endsWith("B") && !result.endsWith("M") && !result.endsWith("K")) {
            result = "N/A";
        }
        return result;
    }

    public String parsePERatio() {
        int target = getTarget(line, "P/E Ratio");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        if ((deci - target) < 70) {
            return line.substring(start + 1, deci + 3);
        }
        return "N/A";
    }

    public String parseEarningsPerShare() {
        int target = getTarget(line, "EPS");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '$') {
            start--;
        }
        if ((deci - target) < 70) { // if N/A then keep EPS as N/A
            return line.substring(start + 1, deci + 3);
        }
        return "N/A";
    }

    public String parsePercentageFloatShorted() {
        int target = getTarget(line, "Float Shorted");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        if ((deci - target) < 70) {
            return line.substring(start + 1, deci + 3);
        }
        return "N/A";
    }

    public String parseAverageVolume() {
        int target = getTarget(line, "Average Volume");
        int deci = line.indexOf(".", target);
        int start = deci;
        while (line.charAt(start) != '>') {
            start--;
        }
        String temp = line.substring(start + 1, deci + 4);
        if (temp.charAt(temp.length() - 1) == '<') {
            return temp.substring(0, temp.length() - 1);
        }
        return temp;
    }

    private int getTarget(String line, String title) {
        return line.indexOf(title);
    }

    private int getDeci(String line, int target) {
        return line.indexOf(".", target);
    }
}
