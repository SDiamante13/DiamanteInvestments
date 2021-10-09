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

        LoginWindow frame = GuiActionRunner.execute(() -> new LoginWindow(mockConnection));
        window = new FrameFixture(robot(), frame);
        window.show();
        window.maximize();
    }

    @Test
    public void theStockFormIsDisplayed_whenTheUsernameAndPasswordCombinationExistsInTheDatabase() throws Exception {
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);

        window.textBox("txtUsername").enterText("BJoel");
        window.textBox("txtPassword").enterText("pianoman");
        window.button("btnLogin").click();
        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(10000).using(robot());
        optionPane.button("OptionPane.button").click();

        assertThat(findFrame("stockWindow").using(robot())).isNotNull();
    }

    @Test
    public void theCreateAccountFormIsDisplayed_whenTheCreateNewAccountButtonIsClicked() {
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
}