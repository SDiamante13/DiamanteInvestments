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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewAccountTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private Statement mockStatement;

    @Override
    protected void onSetUp() {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockStatement = mock(Statement.class);

        initializeWindow();
    }

    @Test
    public void theNewAccountWindowIsClosedAfterSuccessfullyCreatingANewAccount() throws Exception {
        when(mockConnection.prepareStatement(any())).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mock(ResultSet.class));

        window.button("btnNewButton").click();

        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(10000).using(robot());
        optionPane.button("OptionPane.button").click();

        window.requireNotVisible();
    }

    private void initializeWindow() {
        NewAccount frame = GuiActionRunner.execute(() -> new NewAccount(mockConnection));
        window = new FrameFixture(robot(), frame);
        window.show();
        window.maximize();
    }

}