package tech.pathtoprogramming.diamanteinvestments.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRepository {
    private final Connection connection;

    public LoginRepository(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean doesUsernameExist(String username, char[] password) throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement("select * from PortfolioLogins where username=? and password=?");
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, String.valueOf(password));
        ResultSet resultSet = preparedStatement.executeQuery();
        int count = 0;
        while (resultSet.next()) {
            count++;
        }
        preparedStatement.close();
        resultSet.close();

        return count >= 1;
    }
}
