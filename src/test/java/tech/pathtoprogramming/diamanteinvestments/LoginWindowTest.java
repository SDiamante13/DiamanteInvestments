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
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginWindowTest extends AssertJSwingJUnitTestCase {
    private FrameFixture window;
    private final Connection mockConnection = mock(Connection.class);
    private final PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
    private final ResultSet mockResultSet = mock(ResultSet.class);

    @Override
    protected void onSetUp() {
        mockDatabaseConnection();
        initializeWindow();
    }

    @Test
    public void theStockFormIsDisplayedWhenTheUsernameAndPasswordCombinationExistsInTheDatabase() throws Exception {
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);

        logInWithUser();
        dismissDialog();

        assertThat(findFrame("stockWindow").using(robot())).isNotNull();
    }

    @Test
    public void theLoginWindowClosesWhenLoginIsSuccessful() throws Exception {
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);

        logInWithUser();
        dismissDialog();

        window.requireNotVisible();
    }

    @Test
    public void theUserIsReturnedBackToTheLoginWindowOnAFailedLoginAttempt() throws Exception {
        when(mockResultSet.next()).thenReturn(false);

        logInWithUser();
        dismissDialog();

        window.requireVisible();
    }

    @Test
    public void theUserIsReturnedBackToTheLoginWindowWhenMultiplesOfTheUsernameExistsInTheDatabase() throws Exception {
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        logInWithUser();
        dismissDialog();

        window.requireVisible();
    }

    @Test
    public void anErrorDialogIsShownWhenTheDatabaseQueryFails() throws Exception {
        String error = "Something went wrong while attempting log in. Please try again later.";
        when(mockPreparedStatement.executeQuery())
                .thenThrow(new SQLException(error));

        logInWithUser();

        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(10000).using(robot());
        optionPane.requireMessage("java.sql.SQLException: " + error);
        optionPane.requireErrorMessage();
    }

    @Test
    public void theCreateAccountFormIsDisplayedWhenTheCreateNewAccountButtonIsClicked() {
        window.button("btnNewButton").click();

        assertThat(findFrame("newAccountFrame").using(robot())).isNotNull();
    }

    private void mockDatabaseConnection() {
        try {
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeWindow() {
        LoginWindow frame = GuiActionRunner.execute(() -> new LoginWindow(mockConnection));
        window = new FrameFixture(robot(), frame);
        window.show();
        window.maximize();
    }

    private void logInWithUser() {
        window.textBox("txtUsername").enterText("BJoel");
        window.textBox("txtPassword").enterText("pianoman");
        window.button("btnLogin").click();
    }

    private void dismissDialog() {
        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(10000).using(robot());
        optionPane.button("OptionPane.button").click();
    }
}