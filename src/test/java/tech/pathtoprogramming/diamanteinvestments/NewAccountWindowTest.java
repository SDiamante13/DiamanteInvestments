package tech.pathtoprogramming.diamanteinvestments;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import java.sql.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NewAccountWindowTest extends AssertJSwingJUnitTestCase {

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
        
        window.button("btnNewAccount").click();
        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(10000).using(robot());
        optionPane.button("OptionPane.button").click();

        optionPane.requireMessage("New User Created!");
        window.requireNotVisible();
    }

    @Test
    public void anErrorMessageIsDisplayedWhenThePasswordAndConfirmPasswordFieldsDoNotMatch_stillCallsDatabase() throws Exception {
        when(mockConnection.prepareStatement(any())).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        window.textBox("txtPassword").enterText("pianoman");
        window.textBox("txtConfirmPassword").enterText("diffPass13");
        window.button("btnNewAccount").click();

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

        window.button("btnNewAccount").click();

        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(10000).using(robot());
        optionPane.requireMessage("This username is already being used. Please select another username.");
        optionPane.requireErrorMessage();
    }

    @Test
    public void anErrorMessageIsShownWhenTheAccountIsNotAbleToBeInsertedIntoTheDatabase() throws SQLException {
        when(mockConnection.prepareStatement(any())).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.execute()).thenThrow(new SQLException("Connection error occurred"));

        window.button("btnNewAccount").click();

        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(10000).using(robot());
        optionPane.requireMessage("Connection error occurred");
        optionPane.requireErrorMessage();
    }

    private void initializeWindow() {
        NewAccountWindow frame = GuiActionRunner.execute(() -> new NewAccountWindow(mockConnection));
        window = new FrameFixture(robot(), frame);
        window.show();
        window.maximize();
    }
}
