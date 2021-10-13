package tech.pathtoprogramming.diamanteinvestments;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import tech.pathtoprogramming.diamanteinvestments.repository.LoginRepository;

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
    private LoginRepository mockLoginRepository = mock(LoginRepository.class);

    private final String username = "BJoel";
    private final char[] password = "pianoman".toCharArray();

    @Override
    protected void onSetUp() {
        initializeWindow();
    }

    @Test
    public void theStockFormIsDisplayedWhenTheUsernameAndPasswordCombinationExistsInTheDatabase() throws Exception {
        when(mockLoginRepository.doesUsernameExist(username, password)).thenReturn(true);

        logInWithUser();
        dismissDialog();

        assertThat(findFrame("stockWindow").using(robot())).isNotNull();
    }

    @Test
    public void theLoginWindowClosesWhenLoginIsSuccessful() throws Exception {
        when(mockLoginRepository.doesUsernameExist(username, password)).thenReturn(true);

        logInWithUser();
        dismissDialog();

        window.requireNotVisible();
    }

    @Test
    public void theUserIsReturnedBackToTheLoginWindowOnAFailedLoginAttempt() throws Exception {
        when(mockLoginRepository.doesUsernameExist(username, password)).thenReturn(false);

        logInWithUser();
        dismissDialog();

        window.requireVisible();
    }

    @Test
    public void anErrorDialogIsShownWhenTheDatabaseQueryFails() throws Exception {
        String error = "Something went wrong while attempting log in. Please try again later.";
        when(mockLoginRepository.doesUsernameExist(username, password)).thenThrow(new SQLException(error));

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

    private void initializeWindow() {
        LoginWindow frame = GuiActionRunner.execute(() -> new LoginWindow(mockLoginRepository));
        window = new FrameFixture(robot(), frame);
        window.show();
        window.maximize();
    }

    private void logInWithUser() {
        window.textBox("txtUsername").enterText(username);
        window.textBox("txtPassword").enterText(String.valueOf(password));
        window.button("btnLogin").click();
    }

    private void dismissDialog() {
        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(10000).using(robot());
        optionPane.button("OptionPane.button").click();
    }
}