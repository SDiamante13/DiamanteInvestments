package tech.pathtoprogramming.diamanteinvestments;

import java.sql.*;
import javax.swing.*;

public class DBConnection {
	Connection conn = null;
	public static Connection dbConnecter() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection("jdbc:mysql:// localhost:3306/stock_portfolio","sa","password");
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
	}
	
}
