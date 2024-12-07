package orm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import realdb.GhettoJdbcBlackBox;

public class DaoInvocationHandler implements InvocationHandler {

	static GhettoJdbcBlackBox jdbc;
	
	public DaoInvocationHandler() {
		// TODO Auto-generated constructor stub
		
		if (jdbc==null)
		{
			jdbc = new GhettoJdbcBlackBox();
			jdbc.init("com.mysql.cj.jdbc.Driver", 				// DO NOT CHANGE
					  "jdbc:mysql://localhost/jdbcblackbox",    // change jdbcblackbox to the DB name you wish to use
					  "root", 									// USER NAME
					  "");										// PASSWORD
		}
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		// determine method annotation type and call the appropriate method
			// @CreateTable
			// @Save
			// @Delete
			// @Select
			
		return null;
	}
	
	
	// HELPER METHOD: when putting in field values into SQL, strings are in quotes otherwise they go in as is
	private String getValueAsSql(Object o) throws Exception
	{
		if (o.getClass()==String.class)
		{
			return "\""+o+"\"";
		}
		else
		{
			return String.valueOf(o);
		}		
	}
	
	
	// handles @CreateTable
	private void createTable(Method method)
	{
		
// 		SAMPLE SQL 		
//	    CREATE TABLE REGISTRATION (id INTEGER not NULL AUTO_INCREMENT,
//												first VARCHAR(255), 
//												last VARCHAR(255), age INTEGER, PRIMARY KEY ( id ))
		
// 		Using the @MappedClass annotation from method
		// get the required class 		
		// use reflection to check all the fields for @Column
		// use the @Column attributed to generate the required sql statment
		
		
		
// 		Run the sql
		// jdbc.runSQL(SQL STRING);
	}
	
	// handles @Delete
	private void delete(Method method, Object o) throws Exception
	{
// 		SAMPLE SQL		
//  	DELETE FROM REGISTRATION WHERE ID=1
		
		
// 		Using the @MappedClass annotation from method
		// get the required class 		
		// use reflection to check all the fields for @Column
		// find which field is the primary key
		// for the Object o parameter, get the value of the field and use this as the primary value 
		// for the WHERE clause
				// if the primary key field value is null, throw a RuntimeException("no pk value")
		
		
		// run the sql
//		jdbc.runSQL(SQL STRING);
	}
	
	// handles @Save
	private void save(Method method, Object o) throws Exception
	{
// 		Using the @MappedClass annotation from method
		// get the required class 		
		// use reflection to check all the fields for @Column
		// find which field is the primary key
		// for the Object o parameter, get the value of the field
			// if the field is null run the insert(Object o, Class entityClass, String tableName) method
			// if the field is not null run the update(Object o, Class entityClass, String tableName) method

	}

	private void insert(Object o, Class entityClass, String tableName) throws Exception 
	{
		
		
// 		SAMPLE SQL		
//		INSERT INTO table_name (column1, column2, column3, ...)
//		VALUES (value1, value2, value3, ...)	


//		HINT: columnX comes from the entityClass, valueX comes from o 
		
		
// 		run sql		
//		jdbc.runSQL(SQL STRING);
	}

	private void update(Object o, Class entityClass, String tableName) throws IllegalAccessException, Exception {

//		SAMPLE SQL		
//		UPDATE table_name
//		SET column1 = value1, column2 = value2, ...
//		WHERE condition;
		
//		HINT: columnX comes from the entityClass, valueX comes from o 		
		
//		run sql
//		jdbc.runSQL(SQL STRING);
	}

		
	// handles @Select
	private Object select(Method method, Object[] args) throws Exception
	{
		// same style as lab
		
// PART I		
// 		Using the @MappedClass annotation from method
//		get the required class
//		Use this class to extra all the column information (this is the replacement for @Results/@Result)		
//		generate the SELECT QUERY		

// PART II
		
//		this will pull actual values from the DB		
//		List<HashMap<String, Object>> results = jdbc.runSQLQuery(SQL QUERY);

		
		// process list based on getReturnType
		if (method.getReturnType()==List.class)
		{
			List returnValue = new ArrayList();
			
			// create an instance for each entry in results based on mapped class
			// map the values to the corresponding fields in the object
			// DO NOT HARD CODE THE TYPE and FIELDS USE REFLECTION
			
			return returnValue;
		}
		else
		{
			// if not a list return type
			
			// if the results.size() == 0 return null
			// if the results.size() >1 throw new RuntimeException("More than one object matches")
			// if the results.size() == 1
				// create one instance based on mapped class
				// map the values to the corresponding fields in the object
				// DO NOT HARD CODE THE TYPE and FIELDS USE REFLECTION
						
			return null;
		}
	}
	
}
