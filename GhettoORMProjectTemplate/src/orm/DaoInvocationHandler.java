package orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import annotations.*;
import realdb.GhettoJdbcBlackBox;

public class DaoInvocationHandler implements InvocationHandler {

	static GhettoJdbcBlackBox jdbc;
	
	public DaoInvocationHandler() {
		// TODO Auto-generated constructor stub
		
		if (jdbc==null)
		{
			jdbc = new GhettoJdbcBlackBox();
			jdbc.init("com.mysql.cj.jdbc.Driver", 				// DO NOT CHANGE
					  "jdbc:mysql://localhost/jdbcblackbox?useSSL=false",    // change jdbcblackbox to the DB name you wish to use
					  "root", 									// USER NAME
					  "jasperkim");										// PASSWORD
		}
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// determine method annotation type and call the appropriate method
		// @CreateTable
		// @Save
		// @Delete
		// @Select
		
		if (method.isAnnotationPresent(CreateTable.class)) {
            createTable(method);
	    }
	    
	    if (method.isAnnotationPresent(Save.class)) {
            save(method, proxy);
	    }
	    
	    if (method.isAnnotationPresent(Delete.class)) {
            delete(method, proxy);
	    }
	    
	    if (method.isAnnotationPresent(Save.class)) {
            select(method, args);
	    }
			
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
		// private
		
		Class<?> methodClass = method.getDeclaringClass();
		String template = "CREATE TABLE <name> (<fields>  PRIMARY KEY ( <pk> ))";

		if (methodClass.isAnnotationPresent(MappedClass.class))
		{
			Class<?> clazz = methodClass.getAnnotation(MappedClass.class).clazz();
			Entity t = (Entity) clazz.getAnnotation(Entity.class);
			
			String tableName = t.table();
			
			String fieldString = "";
			
			String pkColumn = null;

			Field[] fields = clazz.getDeclaredFields();
			for (Field f : fields)
			{
				if (f.isAnnotationPresent(Column.class))
				{
					Column c = f.getAnnotation(Column.class);
					String name = c.name();
					String sql = c.sqlType();
					boolean pk = c.id();

					if (pk==true)
					{
						pkColumn = name;
					}
					
					fieldString = fieldString + name + " "+sql+", ";
				}
			}
			
			String returnSql = template.replaceAll("<name>", tableName);
			returnSql = returnSql.replaceAll("<fields>", fieldString);
			returnSql = returnSql.replaceAll("<pk>", pkColumn);

			System.out.println(tableName);
			jdbc.runSQL(returnSql);
			
		}
		else
		{
			throw new RuntimeException("No @Table");
		}
		
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
		
	    Class<?> methodClass = method.getDeclaringClass();
	    if (methodClass.isAnnotationPresent(MappedClass.class)) {
	        Class<?> entityClass = methodClass.getAnnotation(MappedClass.class).clazz();
	        Entity entity = entityClass.getAnnotation(Entity.class);
	        String tableName = entity.table();

	        Field primaryKeyField = null;
	        Field[] fields = entityClass.getDeclaredFields();

	        for (Field f : fields) {
	            if (f.isAnnotationPresent(Column.class) && f.getAnnotation(Column.class).id()) {
	                primaryKeyField = f;
	                break;
	            }
	        }

	        if (primaryKeyField == null) {
	            throw new RuntimeException("No primary key field found.");
	        }

	        primaryKeyField.setAccessible(true);
	        Object pkValue = primaryKeyField.get(o);
	        if (pkValue == null) {
	            throw new RuntimeException("No primary key value provided for deletion.");
	        }

	        String sql = String.format("DELETE FROM %s WHERE %s = %s",
	                tableName,
	                primaryKeyField.getAnnotation(Column.class).name(),
	                getValueAsSql(pkValue));

	        jdbc.runSQL(sql);
	    } else {
	        throw new RuntimeException("No @MappedClass annotation found.");
	    }
		
	}
	
	// handles @Save
	private void save(Method method, Object o) throws Exception
	{
		Class<?> methodClass = method.getDeclaringClass();
	    if (methodClass.isAnnotationPresent(MappedClass.class)) {
	        Class<?> entityClass = methodClass.getAnnotation(MappedClass.class).clazz();
	        Entity entity = entityClass.getAnnotation(Entity.class);
	        String tableName = entity.table();

	        Field primaryKeyField = null;
	        Field[] fields = entityClass.getDeclaredFields();

	        for (Field f : fields) {
	            if (f.isAnnotationPresent(Column.class) && f.getAnnotation(Column.class).id()) {
	                primaryKeyField = f;
	                break;
	            }
	        }

	        if (primaryKeyField == null) {
	            throw new RuntimeException("No primary key field found.");
	        }

	        primaryKeyField.setAccessible(true);
	        Object pkValue = primaryKeyField.get(o);

	        if (pkValue == null) {
	            insert(o, entityClass, tableName);
	        } else {
	            update(o, entityClass, tableName);
	        }
	    } else {
	        throw new RuntimeException("No @MappedClass annotation found.");
	    }
	}

	private void insert(Object o, Class entityClass, String tableName) throws Exception 
	{
		
		
		Field[] fields = entityClass.getDeclaredFields();
	    StringBuilder columns = new StringBuilder();
	    StringBuilder values = new StringBuilder();

	    for (Field f : fields) {
	        if (f.isAnnotationPresent(Column.class)) {
	            f.setAccessible(true);
	            Column column = f.getAnnotation(Column.class);

	            if (columns.length() > 0) {
	                columns.append(", ");
	                values.append(", ");
	            }

	            columns.append(column.name());
	            values.append(getValueAsSql(f.get(o)));
	        }
	    }

	    String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);
	    jdbc.runSQL(sql);
	}

	private void update(Object o, Class entityClass, String tableName) throws IllegalAccessException, Exception {

		Field[] fields = entityClass.getDeclaredFields();
	    StringBuilder updates = new StringBuilder();
	    String primaryKey = null;
	    Object primaryKeyValue = null;

	    for (Field f : fields) {
	        f.setAccessible(true);
	        if (f.isAnnotationPresent(Column.class)) {
	            Column column = f.getAnnotation(Column.class);
	            if (column.id()) {
	                primaryKey = column.name();
	                primaryKeyValue = f.get(o);
	            } else {
	                if (updates.length() > 0) {
	                    updates.append(", ");
	                }
	                updates.append(column.name()).append(" = ").append(getValueAsSql(f.get(o)));
	            }
	        }
	    }

	    if (primaryKey == null || primaryKeyValue == null) {
	        throw new RuntimeException("Primary key missing or null for update.");
	    }

	    String sql = String.format("UPDATE %s SET %s WHERE %s = %s",
	            tableName, updates, primaryKey, getValueAsSql(primaryKeyValue));

	    jdbc.runSQL(sql);
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
