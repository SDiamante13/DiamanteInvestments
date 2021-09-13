import java.sql.*;
import javax.swing.*;

public class sqliteConnection {
	Connection conn = null;
	public static Connection dbConnecter() {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\steel\\eclipse-workspace\\Databases\\StockPortfolio.sqlite");
			return conn;
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
	}
	
}
