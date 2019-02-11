package dbase;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBASE {

	private static Connection conn = null;

	public static Connection getConnection() {
		if (conn == null) {

			try {
				Properties properts = loadProperties();
				String url = properts.getProperty("dburl");
				conn = DriverManager.getConnection(url, properts);

			} 
			catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		return conn;
	}

	public static void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	private static Properties loadProperties() {

		try (FileInputStream fileStream = new FileInputStream("db.properties")) {
			Properties properts = new Properties();
			properts.load(fileStream);
			return properts;
			
		} 
		catch (IOException e) {
			throw new DbException(e.getMessage());
		}
	}
	
	public static void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	public static void closeResultSet(ResultSet result) {
		if (result != null) {
			try {
				result.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
}
