package tech.pathtoprogramming.diamanteinvestments;

import lombok.extern.slf4j.Slf4j;
import tech.pathtoprogramming.diamanteinvestments.model.Bounds;
import tech.pathtoprogramming.diamanteinvestments.repository.LoginRepository;

import javax.swing.*;
import javax.xml.bind.ValidationException;
import java.awt.*;
import java.awt.event.ActionListener;

@Slf4j
public class LoginWindow extends JFrame {

    private final LoginRepository loginRepository;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblValidation;

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
                "lblTitle",
                "Diamante Investments",
                new Font("Edwardian Script ITC", Font.BOLD, 30),
                new Bounds(71, 23, 288, 57)
        );
        buildLabel(
                "lblUsername",
                "Username",
                new Font("Tahoma", Font.BOLD, 12),
                new Bounds(71, 103, 64, 26)
        );
        buildLabel(
                "lblPassword",
                "Password",
                new Font("Tahoma", Font.BOLD, 12),
                new Bounds(71, 140, 64, 26)
        );
        buildTextField("txtUsername", new Bounds(145, 103, 96, 23));
        buildPasswordField("txtPassword", new Bounds(145, 143, 96, 23));

        buildValidationLabel();

        buildButton("btnLogin", "Login", new Bounds(81, 192, 160, 26), loginActionListener());
        buildButton("btnNewButton", "Create new account", new Bounds(81, 229, 160, 26), newAccountActionListener());
    }

    private void configureJFrameOptions() {
        this.setBounds(100, 100, 424, 329);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(null);
    }

    private JLabel buildLabel(String id, String text, Font font, Bounds bounds) {
        JLabel lblNewLabel = new JLabel(text);
        lblNewLabel.setName(id);
        lblNewLabel.setFont(font);
        lblNewLabel.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        this.getContentPane().add(lblNewLabel);
        return lblNewLabel;
    }

    private void buildValidationLabel() {
        lblValidation = buildLabel(
                "lblValidation",
                "",
                new Font("Tahoma", Font.ITALIC, 10),
                new Bounds(81, 166, 500, 26)
        );
        lblValidation.setForeground(Color.RED);
        lblValidation.setVisible(false);
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
                if (!isValidInput()) throw new ValidationException("Username and/or password is blank");
                boolean userExists = loginRepository.doesUsernameExist(txtUsername.getText(), txtPassword.getPassword());
                if (userExists) {
                    JOptionPane.showMessageDialog(null, "Username and password is correct");
                    destroy();
                    launchStockForm();
                } else {
                    JOptionPane.showMessageDialog(null, "Username and password is incorrect");
                    clearScreen();
                }
            } catch (ValidationException e) {
                log.error("Login Validation Failure: " + e.getMessage());
                displayValidationErrorLabel(e.getMessage());
            } catch (Exception e) {
                log.error("Login Failure: " + e.getMessage());
                JOptionPane.showMessageDialog(null, e, "Error Occurred", JOptionPane.ERROR_MESSAGE);
            }
        };
    }

    private void launchStockForm() {
        StockForm stockWindow = new StockForm(loginRepository.getConnection(), txtUsername.getText().trim());
        stockWindow.setVisible(true);
        stockWindow.setTitle("Diamante Investments - Stock Portfolio");
        stockWindow.setName("stockWindow");
    }

    private void displayValidationErrorLabel(String text) {
        lblValidation.setVisible(true);
        lblValidation.setText(text);
    }

    private boolean isValidInput() {
        if (txtUsername.getText().equals("") || txtPassword.getPassword().length <= 0) {
            return false;
        }

        return true;
    }

    private void clearScreen() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblValidation.setText("");
        lblValidation.setVisible(false);
    }

    private ActionListener newAccountActionListener() {
        return actionEvent -> {
            NewAccount newUser = new NewAccount();
            newUser.getNewAccountFrame().setVisible(true);
        };
    }
}
