package tech.pathtoprogramming.diamanteinvestments;

import org.approvaltests.awt.AwtApprovals;
import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.junit.Test;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewAccountWindowTest {

    private Connection mockConnection = mock(Connection.class);

    @Test
    public void newAccountWindow() {
        NewAccountWindow newAccountWindow = new NewAccountWindow(mockConnection);
        newAccountWindow.getNewAccountFrame().setVisible(true);

        AwtApprovals.verify(newAccountWindow);
    }
}
