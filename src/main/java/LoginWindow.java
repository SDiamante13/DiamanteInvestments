import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.event.ActionEvent;

public class LoginWindow {

	protected JFrame loginFrame;
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	
	
	/**
	 * Launch the application.
	 * This is the login screen for the Stock Portfolio system
	 * Each user will have their own personalized watchlist
	 * Users that do not have access can easily create a new account
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginWindow window = new LoginWindow();
					window.loginFrame.setVisible(true);
					window.loginFrame.setTitle("Diamante Investments - Login Page");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	Connection connection = null;

	/**
	 * Create the application.
	 */
	public LoginWindow() {
		initialize();
		connection = DBConnection.dbConnecter();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		loginFrame = new JFrame();
		loginFrame.setBounds(100, 100, 424, 329);
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Diamante Investments");
		lblNewLabel.setFont(new Font("Edwardian Script ITC", Font.BOLD, 30));
		lblNewLabel.setBounds(71, 23, 288, 57);
		loginFrame.getContentPane().add(lblNewLabel);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblUsername.setBounds(71, 103, 64, 26);
		loginFrame.getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPassword.setBounds(71, 140, 64, 26);
		loginFrame.getContentPane().add(lblPassword);
		
		txtUsername = new JTextField();
		txtUsername.setBounds(145, 103, 96, 23);
		loginFrame.getContentPane().add(txtUsername);
		txtUsername.setColumns(10);
		
		txtPassword = new JPasswordField();
		txtPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtPassword.setBounds(145, 143, 96, 23);
		loginFrame.getContentPane().add(txtPassword);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String query = "select * from PortfolioLogins where username=? and password=?";
					PreparedStatement pst = connection.prepareStatement(query);
					pst.setString(1, txtUsername.getText());
					pst.setString(2, txtPassword.getText());
					
					ResultSet rs = pst.executeQuery();
					int count = 0;
					while(rs.next()) {
						count++;
					}
					if(count == 1) {
						JOptionPane.showMessageDialog(null, "Username and password is correct");
						loginFrame.dispose();
						StockForm stockWindow = new StockForm(txtUsername.getText().trim());
						stockWindow.setVisible(true);
						stockWindow.setTitle("Diamante Investments - Stock Portfolio");
						
					}
					else if(count > 1) {
						JOptionPane.showMessageDialog(null, "Duplicate username and password");
					}
					else {
						JOptionPane.showMessageDialog(null, "Username and password is incorrect");
					}
					rs.close();
					pst.close();
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(null, e);
				}
			}
		});
		btnLogin.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnLogin.setBounds(81, 192, 160, 26);
		loginFrame.getContentPane().add(btnLogin);
		
		JButton btnNewButton = new JButton("Create new account");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NewAccount newUser = new NewAccount();
				newUser.getNewAccountFrame().setVisible(true);
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnNewButton.setBounds(81, 229, 160, 26);
		loginFrame.getContentPane().add(btnNewButton);
	}
}
