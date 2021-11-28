package tech.pathtoprogramming.diamanteinvestments;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NewAccountTest extends AssertJSwingJUnitTestCase {

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

        initializeWindow();
    }

    @Test
    public void theNewAccountWindowIsClosedAfterSuccessfullyCreatingANewAccount() throws Exception {
        when(mockConnection.prepareStatement(any())).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        window.button("btnNewButton").click();
        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(10000).using(robot());
        optionPane.button("OptionPane.button").click();

        window.requireNotVisible();
    }

    @Test
    public void anErrorMessageIsDisplayedWhenThePasswordAndConfirmPasswordFieldsDoNotMatch_stillCallsDatabase() throws Exception {
        when(mockConnection.prepareStatement(any())).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        window.textBox("txtPassword").enterText("pianoman");
        window.textBox("txtConfirmPassword").enterText("diffPass13");
        window.button("btnNewButton").click();

        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(10000).using(robot());
        optionPane.requireMessage("Passwords must be same to be confirmed. Please try again.");
        optionPane.button("OptionPane.button").click();
        optionPane.requireErrorMessage();
        verify(mockConnection, times(0)).prepareStatement(anyString());
    }

    @Test
    public void anErrorMessageIsDisplayedWhenTheUsernameIsAlreadyTaken() throws Exception {
        when(mockConnection.prepareStatement(any())).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        window.button("btnNewButton").click();

        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(10000).using(robot());
        optionPane.requireMessage("This username is already being used. Please select another username.");
        optionPane.button("OptionPane.button").click();
        optionPane.requireErrorMessage();
    }

    private void initializeWindow() {
        NewAccount frame = GuiActionRunner.execute(() -> new NewAccount(mockConnection));
        window = new FrameFixture(robot(), frame);
        window.show();
        window.maximize();
    }

}