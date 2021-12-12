package tech.pathtoprogramming.diamanteinvestments;

import lombok.extern.slf4j.Slf4j;
import tech.pathtoprogramming.diamanteinvestments.model.Bounds;
import tech.pathtoprogramming.diamanteinvestments.model.UserAccount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.ValidationException;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

@Slf4j
public class NewAccountWindow extends JFrame {

    private final Connection connection;

    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;

    public NewAccountWindow(Connection connection) {
        this.connection = connection;
        initialize();
    }

    private void initialize() {
        configureJFrameOptions();

        this.setBounds(100, 100, 524, 413);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewUser = buildLabel(
                "lblNewUser",
                "",
                null,
                new Bounds(327, 40, 153, 146)
        );

        lblNewUser.setIcon(new ImageIcon(this.getClass().getResource("/newUser.png")));

        buildLabel(
                "lblCreateANew",
                "Create a new account",
                new Font("Calibri", Font.BOLD, 18),
                new Bounds(118, 11, 186, 59)
        );

        buildLabel(
                "lblName",
                "Name",
                new Font("Calibri", Font.BOLD, 14),
                new Bounds(39, 73, 66, 24)
        );

        buildLabel(
                "lblLastName",
                "Last name",
                new Font("Calibri", Font.BOLD, 14),
                new Bounds(39, 108, 66, 24)
        );

        buildLabel(
                "lblEmail",
                "Email",
                new Font("Calibri", Font.BOLD, 14),
                new Bounds(39, 143, 66, 24)
        );

        buildLabel(
                "lblUserName",
                "Username",
                new Font("Calibri", Font.BOLD, 14),
                new Bounds(39, 178, 66, 24)
        );

        buildLabel(
                "lblPassword",
                "Password",
                new Font("Calibri", Font.BOLD, 14),
                new Bounds(39, 213, 66, 24)
        );

        buildLabel(
                "lblConfirmPassword",
                "Confirm Password",
                new Font("Calibri", Font.BOLD, 14),
                new Bounds(39, 248, 115, 38)
        );

        txtFirstName = buildTextField("txtFirstName", new Bounds(163, 75, 115, 20));
        txtLastName = buildTextField("txtLastName", new Bounds(163, 110, 115, 20));
        txtEmail = buildTextField("txtEmail", new Bounds(163, 145, 115, 20));
        txtUsername = buildTextField("txtUsername", new Bounds(163, 180, 115, 20));
        txtPassword = buildPasswordField("txtPassword", new Bounds(163, 213, 115, 22));
        txtConfirmPassword = buildPasswordField("txtConfirmPassword", new Bounds(164, 257, 115, 22));
        buildButton("btnNewAccount", "Create", new Bounds(37, 309, 108, 38), createAccountActionListener());
    }

    private void configureJFrameOptions() {
        this.setName("newAccountFrame");
        this.setBounds(100, 100, 424, 329);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

    private JTextField buildTextField(String id, Bounds bounds) {
        JTextField newTextField = new JTextField();
        newTextField.setName(id);
        newTextField.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        this.getContentPane().add(newTextField);
        newTextField.setColumns(10);
        return newTextField;
    }

    private JPasswordField buildPasswordField(String id, Bounds bounds) {
        JPasswordField newTextField = new JPasswordField();
        newTextField.setName(id);
        newTextField.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        this.getContentPane().add(newTextField);
        newTextField.setColumns(10);
        return newTextField;
    }

    private void buildButton(String id, String text, Bounds bounds, ActionListener actionListener) {
        JButton newButton = new JButton(text);
        newButton.setName(id);
        newButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        newButton.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        newButton.addActionListener(actionListener);
        this.getContentPane().add(newButton);
    }

    private ActionListener createAccountActionListener() {
        return actionEvent -> {
            try {
                UserAccount userAccount = new UserAccount(txtFirstName.getText(), txtLastName.getText(), txtEmail.getText(), txtUsername.getText(), txtPassword.getText(), txtConfirmPassword.getText());

                if (!userAccount.getPassword().equals(userAccount.getConfirmPassword())) { // not same, try again
                    throw new ValidationException("Passwords must be same to be confirmed. Please try again.");
                }

                if (isUsernameTaken(userAccount.getUsername())) {
                    throw new ValidationException("This username is already being used. Please select another username.");
                }


                insertNewAccountIntoDatabase(userAccount);

                createUserWatchlistTable(userAccount.getUsername());

                JOptionPane.showMessageDialog(null, "New User Created!");
                destroy();
            } catch (Exception e) {
                log.error("New Account Failure: ", e);
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error Occurred", JOptionPane.ERROR_MESSAGE);
            }
        };
    }

    public boolean isUsernameTaken(String username) throws SQLException {
        boolean isUsernameTaken = false;
        String query = "select * from PortfolioLogins where username=?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, username);
        ResultSet r1 = pst.executeQuery();
        if (r1.next()) {
            isUsernameTaken = true;
        }
        pst.close();
        r1.close();
        return isUsernameTaken;
    }

    public void insertNewAccountIntoDatabase(UserAccount userAccount) throws SQLException {
        // if no errors, enter new account into database
        String query2 = "insert into PortfolioLogins (Name, Surname, Email, Username, Password, Confirm) values (?, ?, ?, ?, ?, ?)";
        PreparedStatement pst2 = connection.prepareStatement(query2);
        pst2.setString(1, userAccount.getFirstName());
        pst2.setString(2, userAccount.getLastName());
        pst2.setString(3, userAccount.getEmail());
        pst2.setString(4, userAccount.getUsername());
        pst2.setString(5, userAccount.getPassword());
        pst2.setString(6, userAccount.getConfirmPassword());
        pst2.execute();
        pst2.close();
    }

    public void createUserWatchlistTable(String username) throws SQLException {
        //-------------------------------------------------------
        // Create unique User Watchlist table
        Statement stmt = connection.createStatement();
        // Create unique User Watchlist table
        String tableName = username + "WatchList";
        String create = "CREATE TABLE IF NOT EXISTS " + tableName + "(symbol varchar(10) PRIMARY KEY UNIQUE)";
        stmt.execute(create);
        stmt.close();
    }

    private void destroy() {
        this.dispose();
    }

    public JFrame getNewAccountFrame() {
        return this;
    }
}
