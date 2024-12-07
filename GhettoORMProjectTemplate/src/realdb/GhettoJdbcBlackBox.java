package realdb;

// DO NOT DO ANYTHING WITH THIS CLASS


//STEP 1. Import required packages
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GhettoJdbcBlackBox {
	
	
	private String DB_URL;
	private String USER;
	private String PASS;
	private String DRIVER = "com.mysql.jdbc.Driver";

	public void init(String jdbcDriverClass, String url, String userName, String password) {
		DB_URL = url;
		USER = userName;
		PASS = password;
		DRIVER = jdbcDriverClass;

		// STEP 2: Register JDBC driver
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		runSQLQuery("select 1"); // test connection
		System.out.println("Connection ok");
	}

	public void runSQL(String sql) {
		Connection conn = null;
		Statement stmt = null;
		try {

			// STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");

			// STEP 4: Execute a query
			System.out.println("Running SQL: " + sql);
			stmt = conn.createStatement();

			stmt.executeUpdate(sql);
			System.out.println("SQL executed");

		} catch (SQLException se) {
			// Handle errors for JDBC
			throw new RuntimeException(se);
		} catch (Exception e) {
			// Handle errors for Class.forName
			throw new RuntimeException(e);
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
	}// end method 

	public List<HashMap<String, Object>> runSQLQuery(String sql) {

		Connection conn = null;
		Statement stmt = null;

		List<HashMap<String, Object>> returnValue = new ArrayList<HashMap<String, Object>>();

		try {

			// STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");

			// STEP 4: Execute a query
			System.out.println("Creating statement... " + sql);
			stmt = conn.createStatement();

			System.out.println("Running query");
			ResultSet rs = stmt.executeQuery(sql);

			System.out.println("Processing results: ");
			
			while (rs.next()) {

				int columnCount = rs.getMetaData().getColumnCount();

				
				
				HashMap<String, Object> value = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					String key = rs.getMetaData().getColumnName(i);
					Object columnValue = rs.getObject(i);
					
					//System.out.println(key+" "+columnValue);
					// put result into map
					value.put(key, columnValue);
				}
				
				returnValue.add(value);
				
			}
			rs.close();

		} catch (SQLException se) {
			// Handle errors for JDBC
			throw new RuntimeException(se);
		} catch (Exception e) {
			// Handle errors for Class.forName
			throw new RuntimeException(e);
	} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try

		return returnValue;
	} // end method
	
	
	
	public static void main(String[] args)
	{
		GhettoJdbcBlackBox jdbc = new GhettoJdbcBlackBox();
		jdbc.init("com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost/jdbcblackbox", "root", "");
		
		try
		{
		  
// 		  CREATE
		  
	      String sql = "CREATE TABLE REGISTRATION " +
                  "(id INTEGER not NULL AUTO_INCREMENT, " +
                  " first VARCHAR(255), " + 
                  " last VARCHAR(255), " + 
                  " age INTEGER, " + 
                  " PRIMARY KEY ( id ))";
	      
	      jdbc.runSQL(sql);
	      
		}
		catch(Exception e)
		{
			
		}
	      System.out.println(jdbc.runSQLQuery("SELECT * from REGISTRATION"));
	}
}
