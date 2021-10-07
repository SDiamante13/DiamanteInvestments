package tech.pathtoprogramming.diamanteinvestments;

import org.assertj.swing.assertions.Assertions;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.junit.Assert.*;

public class LoginWindowTest extends AssertJSwingJUnitTestCase {
    private FrameFixture window;

    @Override
    protected void onSetUp() {
        LoginWindow frame = GuiActionRunner.execute(LoginWindow::new);

        window = new FrameFixture(robot(), frame);
        window.show();
    }

    @Test
    public void theStockFormIsDisplayed_whenTheUsernameAndPasswordCombinationExistsInTheDatabase() {
        window.textBox("txtUsername").enterText("Billy");
        window.textBox("txtPassword").enterText("Joel");
        window.button("btnLogin").click();

        //FrameFixture mainFrame = findFrame("stockWindow").using(robot());

    }
}