package tech.pathtoprogramming.diamanteinvestments;

import tech.pathtoprogramming.diamanteinvestments.repository.LoginRepository;

import javax.swing.*;
import java.awt.*;

public class DiamanteInvestmentsApplication {

    /**
     * Launch the application.
     * This is the login screen for the Stock Portfolio system
     * Each user will have their own personalized watchlist
     * Users that do not have access can easily create a new account
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoginRepository loginRepository = new LoginRepository(DBConnection.dbConnecter());
                JFrame window = new LoginWindow(loginRepository);
                window.setVisible(true);
                window.setTitle("Diamante Investments - Login Page");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
