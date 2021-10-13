package tech.pathtoprogramming.diamanteinvestments;

import tech.pathtoprogramming.diamanteinvestments.model.Bounds;
import tech.pathtoprogramming.diamanteinvestments.repository.LoginRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginWindow extends JFrame {

    private final LoginRepository loginRepository;

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginWindow(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        initialize();
    }

    private void destroy() {
        this.dispose();
    }

    private void initialize() {
        configureJFrameOptions();

        buildLabel(
                "Diamante Investments",
                new Font("Edwardian Script ITC", Font.BOLD, 30),
                new Bounds(71, 23, 288, 57)
        );
        buildLabel(
                "Username",
                new Font("Tahoma", Font.BOLD, 12),
                new Bounds(71, 103, 64, 26)
        );
        buildLabel(
                "Password",
                new Font("Tahoma", Font.BOLD, 12),
                new Bounds(71, 140, 64, 26)
        );
        buildTextField("txtUsername", new Bounds(145, 103, 96, 23));
        buildPasswordField("txtPassword", new Bounds(145, 143, 96, 23));

        buildButton("btnLogin", "Login", new Bounds(81, 192, 160, 26), loginActionListener());
        buildButton("btnNewButton", "Create new account", new Bounds(81, 229, 160, 26), newAccountActionListener());
    }

    private void configureJFrameOptions() {
        this.setBounds(100, 100, 424, 329);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(null);
    }

    private void buildLabel(String text, Font font, Bounds bounds) {
        JLabel lblNewLabel = new JLabel(text);
        lblNewLabel.setFont(font);
        lblNewLabel.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        this.getContentPane().add(lblNewLabel);
    }

    private void buildTextField(String id, Bounds bounds) {
        txtUsername = new JTextField();
        txtUsername.setName(id);
        txtUsername.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        txtUsername.setColumns(10);
        this.getContentPane().add(txtUsername);
    }

    private void buildPasswordField(String id, Bounds bounds) {
        txtPassword = new JPasswordField();
        txtPassword.setName(id);
        txtPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtPassword.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        this.getContentPane().add(txtPassword);
    }

    private void buildButton(String id, String text, Bounds bounds, ActionListener actionListener) {
        JButton btnLogin = new JButton(text);
        btnLogin.setName(id);
        btnLogin.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnLogin.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        btnLogin.addActionListener(actionListener);
        this.getContentPane().add(btnLogin);
    }

    private ActionListener loginActionListener() {
        return actionEvent -> {
            try {
                if (loginRepository.doesUsernameExist(txtUsername.getText(), txtPassword.getPassword())) {
                    JOptionPane.showMessageDialog(null, "Username and password is correct");
                    destroy();

                    StockForm stockWindow = new StockForm(loginRepository.getConnection(), txtUsername.getText().trim());
                    stockWindow.setVisible(true);
                    stockWindow.setTitle("Diamante Investments - Stock Portfolio");
                    stockWindow.setName("stockWindow");
                } else {
                    JOptionPane.showMessageDialog(null, "Username and password is incorrect");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e, "Error Occurred", JOptionPane.ERROR_MESSAGE);
            }
        };
    }

    private ActionListener newAccountActionListener() {
        return actionEvent -> {
            NewAccount newUser = new NewAccount();
            newUser.getNewAccountFrame().setVisible(true);
        };
    }
}
