package tech.pathtoprogramming.diamanteinvestments;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StockFormTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private Statement mockStatement;
    private ResultSet mockResultSet;

    @Override
    protected void onSetUp() {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockStatement = mock(Statement.class);
        mockResultSet = mock(ResultSet.class);

        mockDatabase();

        initializeWindow();
    }

    // tests
    // searchStocks
    // add StocksToWatchList
    // remove Stocks form watchlist
    // View stock chart
    // Update watchlist


    @Test
    public void anErrorMessageIsDisplayedWhenSearchStockButtonIsPressedWithNoStockToSearch() throws Exception {
        window.button("btnSearchStock").click();

        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(10000).using(robot());
        optionPane.button("OptionPane.button").click();

        optionPane.requireMessage("Please enter a valid stock symbol to search.");
    }

    private void mockDatabase() {
        try {
            when(mockConnection.prepareStatement(any())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeWindow() {
        StockForm frame = GuiActionRunner.execute(() -> new StockForm(mockConnection, "BJoel"));
        window = new FrameFixture(robot(), frame);
        window.show();
        window.maximize();
    }
}