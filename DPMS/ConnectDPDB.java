//Created by Deontae Cocroft
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDPDB{
	
	public static Connection getConnection() throws SQLException {
		Connection connection = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost/test?user=postgres&password=Char8305");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new SQLException("Failed to connect to the database.");
		}
		return connection;
	}
	
}



		
