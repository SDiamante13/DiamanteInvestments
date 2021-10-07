package tech.pathtoprogramming.diamanteinvestments;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class AcceptanceTest {

    private FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

//    @BeforeEach
//    public void setUp() {
//        LoginWindow frame = GuiActionRunner.execute(() -> new LoginWindow());
//        window = new FrameFixture(frame);
//        window.show(); // shows the frame to test
//    }
}
