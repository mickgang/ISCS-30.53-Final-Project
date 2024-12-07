package orm;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

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
		try (ScanResult scanResult = new ClassGraph().acceptPackages("dao").enableClassInfo().scan()) {
			scanResult.getClassesWithAnnotation("orm.annotations.MappedClass").forEach(mapperClassInfo -> {
				try {
					Class<?> mapperClass = mapperClassInfo.loadClass();
					MappedClass mappedClass = mapperClass.getAnnotation(MappedClass.class);
					Class<?> entityClass = mappedClass.clazz();

					// check if the clazz has the @Entity annotation
					if (!entityClass.isAnnotationPresent(Entity.class)) {
						throw new RuntimeException("No @Entity on class " + entityClass.getName());
					}

					// map the clazz to the mapper class
					entityToMapperMap.put(entityClass, mapperClass);
				} catch (Exception e) {
					throw new RuntimeException("Error processing @MappedClass: " + e.getMessage(), e);
				}
			});
		}
	}
	

	private void scanEntities() throws ClassNotFoundException 
	{
		// use FastClasspathScanner to scan the entity package for @Entity
		try (ScanResult scanResult = new ClassGraph().acceptPackages("entity").enableClassInfo().scan()) {
			scanResult.getClassesWithAnnotation("orm.annotations.Entity").forEach(entityClassInfo -> {
				try {
					Class<?> entityClass = entityClassInfo.loadClass();
					long idCount = Arrays.stream(entityClass.getDeclaredFields())
						.filter(field -> field.isAnnotationPresent(Column.class))
						.filter(field -> field.getAnnotation(Column.class).id())
						.count();

					// check if there is only 1 field with a Column id attribute
					if (idCount != 1) {
						// if more than one field has id throw new RuntimeException("duplicate id=true")
						throw new RuntimeException("duplicate id=true in class " + entityClass.getName());
					}
				} catch (Exception e) {
					throw new RuntimeException("Error processing @Entity: " + e.getMessage(), e);
				}
			});
		}
	}
	
	
	public Object getMapper(Class clazz)
	{
		// create the proxy object for the mapper class supplied in clazz parameter
		Class<?> mapperClass = entityToMapperMap.get(clazz);
		if (mapperClass == null) {
			throw new RuntimeException("No mapper found for class " + clazz.getName());
		}

		// all proxies will use the supplied DaoInvocationHandler as the InvocationHandler
		return Proxy.newProxyInstance(
			mapperClass.getClassLoader(),
			new Class<?>[]{mapperClass},
			new DaoInvocationHandler()
		);
	}
	

	private void createTables()
	{
		// go through all the Mapper classes in the map
		entityToMapperMap.forEach((entityClass, mapperClass) -> {
			try {
				// create a proxy instance for each
				Object proxy = Proxy.newProxyInstance(
					mapperClass.getClassLoader(),
					new Class<?>[]{mapperClass},
					new DaoInvocationHandler()
				);

				// all these proxies can be casted to BasicMapper
				BasicMapper mapper = (BasicMapper) proxy;

				// run the createTable() method on each of the proxies
				mapper.createTable();
			} catch (Exception e) {
				throw new RuntimeException("Error creating table for " + entityClass.getName() + ": " + e.getMessage(), e);
			}
		});
	}
}
