package tech.pathtoprogramming.diamanteinvestments;

import org.approvaltests.awt.AwtApprovals;
import org.junit.Test;
import tech.pathtoprogramming.diamanteinvestments.repository.LoginRepository;

import static org.mockito.Mockito.mock;

public class LoginWindowTest {

    private final LoginRepository mockLoginRepository = mock(LoginRepository.class);

    @Test
    public void loginScreenIsDisplayedOnStartUp() {
        LoginWindow loginWindow = new LoginWindow(mockLoginRepository);
        loginWindow.setVisible(true);
        loginWindow.setTitle("Diamante Investments - Login Page");

        AwtApprovals.verify(loginWindow);
    }
}