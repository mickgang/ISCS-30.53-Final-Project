package orm;

import java.util.HashMap;

public class MyORM 
{	
	
	HashMap<Class, Class> entityToMapperMap = new HashMap<Class, Class>();
	
	
	public void init() throws Exception
	{
		// scan all mappers -- @MappedClass
		scanMappers();		
		
		// scan all the entities -- @Entity
		scanEntities();
				
		// create all entity tables
		createTables();

	}


	private void scanMappers() throws ClassNotFoundException 
	{
		// use FastClasspathScanner to scan the dao package for @MappedClass
		// check if the clazz has the @Entity annotation
			// if not throw new RuntimeException("No @Entity")
		// map the clazz to the mapper class

	}
	

	private void scanEntities() throws ClassNotFoundException 
	{
		// use FastClasspathScanner to scan the entity package for @Entity
			// go through each of the fields 
			// check if there is only 1 field with a Column id attribute
			// if more than one field has id throw new RuntimeException("duplicate id=true")
		
		
	}
	
	
	public Object getMapper(Class clazz)
	{
		// create the proxy object for the mapper class supplied in clazz parameter
		// all proxies will use the supplied DaoInvocationHandler as the InvocationHandler

		return null;
	}
	

	private void createTables()
	{
		// go through all the Mapper classes in the map
			// create a proxy instance for each
			// all these proxies can be casted to BasicMapper
			// run the createTable() method on each of the proxies
		
	}

	

	
	
}
