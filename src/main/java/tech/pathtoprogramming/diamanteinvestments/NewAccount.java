package tech.pathtoprogramming.diamanteinvestments;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Image;

import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class NewAccount {

	private JFrame newAccountFrame;
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtLastName;
	private JTextField txtEmail;
	private JTextField txtUsername;
	

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewAccount account = new NewAccount();
					//a.loginFrame.setVisible(true);
					account.newAccountFrame.setVisible(true);
					account.newAccountFrame.setTitle("Diamante Investments - New Account Creation");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	Connection connection = null;
	private JPasswordField txtPassword;
	private JPasswordField txtConfirmPassword;

	/**
	 * Create the frame.
	 */
	public NewAccount() {
		newAccountFrame = new JFrame();
		newAccountFrame.setBounds(100, 100, 424, 329);
		newAccountFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		newAccountFrame.getContentPane().setLayout(null);
		connection = DBConnection.dbConnecter();
		newAccountFrame.setBounds(100, 100, 524, 413);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		newAccountFrame.setContentPane(contentPane);
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
		
		JButton btnNewButton = new JButton("Create");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					//-------------------------------------------------------------------
					// check if Password and create password are same
					boolean error = false;
					if(!txtPassword.getText().equals(txtConfirmPassword.getText())) { // not same, try again
						JOptionPane.showMessageDialog(null, "Passwords must be same to be confirmed. Please try again.");
						error = true;
					}
					//--------------------------------------------------------------------
					// check if username is unique
					String query = "select * from PortfolioLogins where username=?";
					PreparedStatement pst = connection.prepareStatement(query);
					pst.setString(1, txtUsername.getText());
					ResultSet r1 = pst.executeQuery();
						while(r1.next()){   
							JOptionPane.showMessageDialog(null, "This username is already being used. Please select another username.");
							error = true;
						}
						if(!error) { // no errors, eneter new account into SQLite
							String query2 = "insert into PortfolioLogins (Name, Surname, Email, Username, Password, Confirm) values (?, ?, ?, ?, ?, ?)";
							PreparedStatement pst2 = connection.prepareStatement(query2);
							pst2.setString(1, txtName.getText());
							pst2.setString(2, txtLastName.getText());
							pst2.setString(3, txtEmail.getText());
							pst2.setString(4, txtUsername.getText());
							pst2.setString(5, txtPassword.getText());
							pst2.setString(6, txtConfirmPassword.getText());
							pst2.execute();
					
					//-------------------------------------------------------
					// Create unique User Watchlist table
					
					Statement stmt = connection.createStatement();
					// Create unique User Watchlist table
					String tableName = txtUsername.getText() + "WatchList";
					String create = "CREATE TABLE IF NOT EXISTS " + tableName + "(symbol varchar(10) PRIMARY KEY UNIQUE)";
					stmt.execute(create);
					
					JOptionPane.showMessageDialog(null, "New User Created!");
					pst.close();
					r1.close();
					pst2.close();
					stmt.close();
					newAccountFrame.dispose();
					}
							}
				catch(Exception e) {
					e.printStackTrace();
					
				}}
		});
		btnNewButton.setFont(new Font("Calibri", Font.BOLD, 14));
		btnNewButton.setBounds(37, 309, 108, 38);
		contentPane.add(btnNewButton);
		
		JLabel lblNewUser = new JLabel("");
		Image imageNewUser = new ImageIcon(this.getClass().getResource("/newUser.png")).getImage();
		lblNewUser.setIcon(new ImageIcon(imageNewUser));
		lblNewUser.setBounds(327, 40, 153, 146);
		contentPane.add(lblNewUser);
		
		txtPassword = new JPasswordField();
		txtPassword.setBounds(163, 213, 115, 22);
		contentPane.add(txtPassword);
		
		txtConfirmPassword = new JPasswordField();
		txtConfirmPassword.setBounds(164, 257, 115, 22);
		contentPane.add(txtConfirmPassword);
	}
	
	public JFrame getNewAccountFrame() {
		return newAccountFrame;
	}

}
