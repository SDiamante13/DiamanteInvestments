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
    private JLabel titleLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel validationLabel;
    private JButton loginButton;
    private JButton createNewAccountButton;

    public LoginWindow(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        initialize();
    }

    private void destroy() {
        this.dispose();
    }

    private void initialize() {
        configureJFrameOptions();
        createLabels();
        createTextFields();
        createButtons();

        UICreator.addAllToContentPane(
                this.getContentPane(),
                titleLabel,
                usernameLabel,
                passwordLabel,
                txtUsername,
                txtPassword,
                validationLabel,
                loginButton,
                createNewAccountButton
        );
    }

    private void configureJFrameOptions() {
        this.setBounds(100, 100, 424, 329);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(null);
    }

    private void createLabels() {
        titleLabel = UICreator.createLabel(
                "lblTitle",
                "Diamante Investments",
                new Font("Edwardian Script ITC", Font.BOLD, 30),
                new Bounds(71, 23, 288, 57)
        );
        usernameLabel = UICreator.createLabel(
                "lblUsername",
                "Username",
                new Font("Tahoma", Font.BOLD, 12),
                new Bounds(71, 103, 64, 26)
        );
        passwordLabel = UICreator.createLabel(
                "lblPassword",
                "Password",
                new Font("Tahoma", Font.BOLD, 12),
                new Bounds(71, 140, 64, 26)
        );
        validationLabel = UICreator.createLabel(
                "lblValidation",
                "",
                new Font("Tahoma", Font.ITALIC, 10),
                new Bounds(81, 166, 500, 26)
        );
        validationLabel.setForeground(Color.RED);
        validationLabel.setVisible(false);
    }

    private void createTextFields() {
        txtUsername = UICreator.createTextField("txtUsername", new Bounds(145, 103, 96, 23));
        txtPassword = UICreator.createPasswordField("txtPassword", new Bounds(145, 143, 96, 23));
    }

    private void createButtons() {
        loginButton = UICreator.createButton("btnLogin", "Login", new Bounds(81, 192, 160, 26), loginActionListener());
        createNewAccountButton = UICreator.createButton("btnNewButton", "Create new account", new Bounds(81, 229, 160, 26), newAccountActionListener());
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

    private ActionListener newAccountActionListener() {
        return actionEvent -> {
            NewAccountWindow newUser = new NewAccountWindow(loginRepository.getConnection());
            newUser.getNewAccountFrame().setVisible(true);
        };
    }

    private void launchStockForm() {
        StockForm stockWindow = new StockForm(loginRepository.getConnection(), txtUsername.getText().trim());
        stockWindow.setVisible(true);
        stockWindow.setTitle("Diamante Investments - Stock Portfolio");
        stockWindow.setName("stockWindow");
    }

    private void displayValidationErrorLabel(String text) {
        validationLabel.setVisible(true);
        validationLabel.setText(text);
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
        validationLabel.setText("");
        validationLabel.setVisible(false);
    }
}
