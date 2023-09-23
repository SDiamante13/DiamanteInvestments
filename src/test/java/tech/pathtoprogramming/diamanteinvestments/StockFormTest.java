package tech.pathtoprogramming.diamanteinvestments;

import org.approvaltests.awt.AwtApprovals;
import org.junit.Test;

import java.sql.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StockFormTest {

    Connection mockConnection = mock(Connection.class);
    PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
    Statement mockStatement = mock(Statement.class);
    ResultSet mockResultSet = mock(ResultSet.class);

    @Test
    public void stockFrom() throws Exception {
        mockDatabase();
        StockForm stockForm = new StockForm(mockConnection, "StevenDiamante");
        stockForm.setVisible(true);
        stockForm.getContentPane().setVisible(true);
        stockForm.setTitle("Diamante Investments - Stock Portfolio");
        stockForm.setName("stockWindow");

        Thread.sleep(2000);

        AwtApprovals.verify(stockForm);
    }

    private void mockDatabase() throws Exception {
        when(mockConnection.prepareStatement(any())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
    }
}