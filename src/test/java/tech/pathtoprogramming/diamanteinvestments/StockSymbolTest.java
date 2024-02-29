package tech.pathtoprogramming.diamanteinvestments;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.QuietReporter;
import org.approvaltests.reporters.UseReporter;
import org.approvaltests.utils.VerifiableMarkdownTable;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@UseReporter(QuietReporter.class)
public class StockSymbolTest {

    private static WireMockServer mockMarketWatchServer;
    private static WireMockServer mockAlphaAdvantageServer;

    @BeforeClass
    public static void beforeClass() {
        mockMarketWatchServer = new WireMockServer(1234);
        mockMarketWatchServer.start();

        mockAlphaAdvantageServer = new WireMockServer(2345);
        mockAlphaAdvantageServer.start();
    }

    @AfterClass
    public static void afterClass() {
        mockMarketWatchServer.stop();
        mockAlphaAdvantageServer.stop();
    }

    @Test
    public void fetchesStockData() throws Exception {
        givenMarketWatchProvidesStockData("stockSymbol.html");
        givenAlphaAdvantageAPIProvidesTimeSeriesData("timeSeriesDaily.csv");

        StockSymbol stockSymbol = new StockSymbol("NFLX",
                "http://localhost:1234",
                "http://localhost:2345");

        Approvals.verify(createTableFor(stockSymbol));
    }

    private void givenAlphaAdvantageAPIProvidesTimeSeriesData(String fileName) throws Exception {
        mockAlphaAdvantageServer.stubFor(get(anyUrl())
                .willReturn(aResponse()
                        .withBody(readFile(fileName))));
    }

    private void givenMarketWatchProvidesStockData(String fileName) throws Exception {
        mockMarketWatchServer.stubFor(get(anyUrl())
                .willReturn(aResponse()
                        .withBody(readFile(fileName))));
    }

    private String readFile(String fileName) throws Exception {
        URL resource = this.getClass().getResource("../../../" + fileName);
        File file = new File(resource.toURI());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private VerifiableMarkdownTable createTableFor(StockSymbol stockSymbol) {
        VerifiableMarkdownTable table = VerifiableMarkdownTable.withHeaders(
                "Open", "Close", "Day's Range", "52 Week Range", "Volume", "Average Volume",
                "Market Cap", "P/E Ratio", "EPS", "Float Shorted", "50-Day Moving Average", "100-Day Moving Average");
        table.addRow(stockSymbol.getOpen(), stockSymbol.getClose(), stockSymbol.getLow() + " - " + stockSymbol.getHigh(),
                stockSymbol.getYearlyLow() + " - " + stockSymbol.getYearlyHigh(), stockSymbol.getVolume(),
                stockSymbol.getAverageVolume(), stockSymbol.getMarketCap(), stockSymbol.getPeRatio(),
                stockSymbol.getEps(), stockSymbol.getFloatShorted(),
                stockSymbol.getFiftyDayMA(), stockSymbol.getHundredDayMA());
        return table;
    }
}
