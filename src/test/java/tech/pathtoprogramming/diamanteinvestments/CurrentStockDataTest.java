package tech.pathtoprogramming.diamanteinvestments;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class CurrentStockDataTest {

    @Test
    public void parseStockName() {
        CurrentStockData currentStockData = new CurrentStockData("<h1 class=\"company__name\">Apple Inc.</h1>",
                "", "", "", "", "");

        assertThat(currentStockData.parseStockName()).isEqualTo("Apple Inc.");
    }

    @Test
    public void throwsIllegalArgumentExceptionWhenCompanyNameIsNotPresent() {
        CurrentStockData currentStockData = new CurrentStockData("",
                "", "", "", "", "");

        assertThatThrownBy(currentStockData::parseStockName)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parseCurrentPrice() {
        CurrentStockData currentStockData = new CurrentStockData("",
                "<div class=\"intraday__data\"> \n <h2 class=\"intraday__price \"> " +
                        "<sup class=\"character\">$</sup> <bg-quote class=\"value\" field=\"Last\" format=\"0,0.00\" " +
                        "channel=\"/zigman2/quotes/202934861/composite,/zigman2/quotes/202934861/lastsale\" " +
                        "session=\"after\">\n   170.39\n  </bg-quote> </h2> <bg-quote channel=\"/zigman2/quotes/202934861/composite\"" +
                        " session=\"after\" class=\"intraday__change negative\"> \n  <i class=\"icon--caret\"></i> \n  " +
                        "<span class=\"change--point--q\">\n   <bg-quote field=\"change\" format=\"0,0.00\" channel=\"/zigman2/quotes/202934861/composite\"" +
                        " session=\"after\">\n    -0.04\n   </bg-quote></span> \n  <span class=\"change--percent--q\">\n " +
                        "  <bg-quote field=\"percentchange\" format=\"0,0.00%\" channel=\"/zigman2/quotes/202934861/composite\"" +
                        " session=\"after\">\n -0.02%\n   </bg-quote></span> \n </bg-quote> \n</div>",
                "", "", "", "");

        assertThat(currentStockData.parsePrice()).isEqualTo("\n   170.39");
    }

    @Test
    public void parseChangeInDollars() {
        CurrentStockData currentStockData = new CurrentStockData("",
                "",
                "<span class=\"change--point--q\">\n <bg-quote field=\"change\" " +
                        "format=\"0,0.00\" channel=\"/zigman2/quotes/202934861/composite\" session=\"after\">\n  -0.04\n " +
                        "</bg-quote></span>", "", "", "");

        assertThat(currentStockData.parseChangeInDollars()).isEqualTo("-0.04");
    }

    @Test
    public void parseChangePercentage() {
        CurrentStockData currentStockData = new CurrentStockData("",
                "", "",
                "<span class=\"change--percent--q\">\n <bg-quote field=\"percentchange\" " +
                        "format=\"0,0.00%\" channel=\"/zigman2/quotes/202934861/composite\" session=\"after\">\n  -0.02%\n " +
                        "</bg-quote></span>", "", "");

        assertThat(currentStockData.parseChangePercentage()).isEqualTo("-0.02");
    }

    @Test
    public void parseClose() {
        CurrentStockData currentStockData = new CurrentStockData("",
                "", "", "",
                "<tbody class=\"remove-last-border\"> \n <tr class=\"table__row\"> \n  " +
                        "<td class=\"table__cell u-semi\">$170.43 </td> \n  <td class=\"table__cell negative\">-1.53</td> \n  " +
                        "<td class=\"table__cell negative\">-0.89%</td> \n </tr> \n</tbody>", "");

        assertThat(currentStockData.parseClose()).isEqualTo("170.43");
    }

    @Test
    public void parseVolume() {
        CurrentStockData currentStockData = new CurrentStockData("",
                "", "", "", "",
                "<div class=\"range__header\"> <span class=\"primary\">Volume: 66.92M</span> " +
                        "<span class=\"secondary\">65 Day Avg: 58.2M</span> \n</div>\n<div class=\"range__header\"> " +
                        "<span class=\"primary\">169.05</span> <span class=\"secondary\">Day Range</span> " +
                        "<span class=\"primary\">173.04</span> \n</div>\n<div class=\"range__header\"> " +
                        "<span class=\"primary\">124.17</span> <span class=\"secondary\">52 Week Range</span> " +
                        "<span class=\"primary\">198.23</span> \n</div>");

        assertThat(currentStockData.parseVolume()).isEqualTo("Volume: 66.92M");
    }
}