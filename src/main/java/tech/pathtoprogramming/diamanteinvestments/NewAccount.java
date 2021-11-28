package tech.pathtoprogramming.diamanteinvestments;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.ValidationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

@Slf4j
public class NewAccount extends JFrame {

    private JPanel contentPane;
    private JTextField txtName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtUsername;

    Connection connection;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;

    /**
     * Create the frame.
     */
    public NewAccount() {
        this.connection = DBConnection.dbConnecter();
        initialize();
    }

    public NewAccount(Connection connection) {
        this.connection = connection;
        initialize();
    }

    private void initialize() {
        this.setName("newAccountFrame");
        this.setBounds(100, 100, 424, 329);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().setLayout(null);
        this.setBounds(100, 100, 524, 413);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblCreateANew = new JLabel("Create a new account");
        lblCreateANew.setFont(new Font("Calibri", Font.BOLD, 18));
        lblCreateANew.setBounds(118, 11, 186, 59);
        contentPane.add(lblCreateANew);

        JLabel lblName = new JLabel("Name");
        lblName.setFont(new Font("Calibri", Font.BOLD, 14));
        lblName.setBounds(39, 73, 66, 24);
        contentPane.add(lblName);

        JLabel lblLastName = new JLabel("Last name");
        lblLastName.setFont(new Font("Calibri", Font.BOLD, 14));
        lblLastName.setBounds(39, 108, 66, 24);
        contentPane.add(lblLastName);

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(new Font("Calibri", Font.BOLD, 14));
        lblEmail.setBounds(39, 143, 66, 24);
        contentPane.add(lblEmail);


        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Calibri", Font.BOLD, 14));
        lblUsername.setBounds(39, 178, 66, 24);
        contentPane.add(lblUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Calibri", Font.BOLD, 14));
        lblPassword.setBounds(39, 213, 66, 24);
        contentPane.add(lblPassword);

        JLabel lblConfirmPassword = new JLabel("Confirm Password");
        lblConfirmPassword.setFont(new Font("Calibri", Font.BOLD, 14));
        lblConfirmPassword.setBounds(39, 248, 115, 38);
        contentPane.add(lblConfirmPassword);

        txtName = new JTextField();
        txtName.setBounds(163, 75, 115, 20);
        contentPane.add(txtName);
        txtName.setColumns(10);

        txtLastName = new JTextField();
        txtLastName.setColumns(10);
        txtLastName.setBounds(163, 110, 115, 20);
        contentPane.add(txtLastName);

        txtEmail = new JTextField();
        txtEmail.setColumns(10);
        txtEmail.setBounds(163, 145, 115, 20);
        contentPane.add(txtEmail);

        txtUsername = new JTextField();
        txtUsername.setColumns(10);
        txtUsername.setBounds(163, 180, 115, 20);
        contentPane.add(txtUsername);

        JLabel lblNewUser = new JLabel("");
        Image imageNewUser = new ImageIcon(this.getClass().getResource("/newUser.png")).getImage();
        lblNewUser.setIcon(new ImageIcon(imageNewUser));
        lblNewUser.setBounds(327, 40, 153, 146);
        contentPane.add(lblNewUser);

        txtPassword = new JPasswordField();
        txtPassword.setName("txtPassword");
        txtPassword.setBounds(163, 213, 115, 22);
        contentPane.add(txtPassword);

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setName("txtConfirmPassword");
        txtConfirmPassword.setBounds(164, 257, 115, 22);
        contentPane.add(txtConfirmPassword);

        JButton btnNewButton = new JButton("Create");
        btnNewButton.addActionListener(createAccountActionListener());
        btnNewButton.setFont(new Font("Calibri", Font.BOLD, 14));
        btnNewButton.setBounds(37, 309, 108, 38);
        btnNewButton.setName("btnNewButton");
        contentPane.add(btnNewButton);
    }

    private ActionListener createAccountActionListener() {
        return actionEvent -> {
            try {
                if (!txtPassword.getText().equals(txtConfirmPassword.getText())) { // not same, try again
                    throw new ValidationException("Passwords must be same to be confirmed. Please try again.");
                }

                if (isUsernameTaken()) {
                    throw new ValidationException("This username is already being used. Please select another username.");
                }

                insertNewAccountIntoDatabase();

                createUserWatchlistTable();

                JOptionPane.showMessageDialog(null, "New User Created!");
                destroy();
            } catch (Exception e) {
                log.error("New Account Failure: ", e);
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error Occurred", JOptionPane.ERROR_MESSAGE);
            }
        };
    }

    public boolean isUsernameTaken() throws SQLException {
        boolean isUsernameTaken = false;
        String query = "select * from PortfolioLogins where username=?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, txtUsername.getText());
        ResultSet r1 = pst.executeQuery();
        if (r1.next()) {
            isUsernameTaken = true;
        }
        pst.close();
        r1.close();
        return isUsernameTaken;
    }

    public void insertNewAccountIntoDatabase() throws SQLException {
        // if no errors, enter new account into database
        String query2 = "insert into PortfolioLogins (Name, Surname, Email, Username, Password, Confirm) values (?, ?, ?, ?, ?, ?)";
        PreparedStatement pst2 = connection.prepareStatement(query2);
        pst2.setString(1, txtName.getText());
        pst2.setString(2, txtLastName.getText());
        pst2.setString(3, txtEmail.getText());
        pst2.setString(4, txtUsername.getText());
        pst2.setString(5, txtPassword.getText());
        pst2.setString(6, txtConfirmPassword.getText());
        pst2.execute();
        pst2.close();
    }

    public void createUserWatchlistTable() throws SQLException {
        //-------------------------------------------------------
        // Create unique User Watchlist table
        Statement stmt = connection.createStatement();
        // Create unique User Watchlist table
        String tableName = txtUsername.getText() + "WatchList";
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
