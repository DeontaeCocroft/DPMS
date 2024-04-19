package src;
//Created by Deontae Cocroft
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//This file is responsible for the connection of the PostgreSQL Database

public class ConnectDB{
	
	public static Connection getConnection() throws SQLException {
		Connection connection = null;
		try {
			Class.forName("org.postgresql.Driver");
			//connection = DriverManager.getConnection("jdbc:postgresql://project-database.cx0s8y2ymcyy.us-east-2.rds.amazonaws.com/DPMS", "Taec_3", "Char8305030802$");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost/test", "postgres", "Char8305");

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new SQLException("Failed to connect to the database.");
		}
		return connection;
	}
	
}