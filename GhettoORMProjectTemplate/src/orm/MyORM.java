package orm;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotations.Column;
import annotations.Entity;
import annotations.MappedClass;
import dao.BasicMapper;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class MyORM {
    HashMap<Class, Class> entityToMapperMap = new HashMap<>();

    public void init() throws Exception {
        // Scan all mappers -- @MappedClass
        scanMappers();

        // Scan all the entities -- @Entity
        scanEntities();

        // Create all entity tables
        createTables();
    }

    private void scanMappers() throws ClassNotFoundException {
        // Use FastClasspathScanner to scan the dao package for @MappedClass
        ScanResult results = new FastClasspathScanner("dao").scan();
        List<String> mapperClasses = results.getNamesOfClassesWithAnnotation(MappedClass.class);

        for (String mapperClassName : mapperClasses) {
            Class<?> mapperClass = Class.forName(mapperClassName);
            MappedClass mappedClassAnnotation = mapperClass.getAnnotation(MappedClass.class);
            if (mappedClassAnnotation != null) {
                Class<?> entityClass = mappedClassAnnotation.clazz();

                // Check if the entity class has @Entity annotation
                if (!entityClass.isAnnotationPresent(Entity.class)) {
                    throw new RuntimeException("No @Entity annotation found on " + entityClass.getName());
                }

                // Map the entity class to the mapper class
                entityToMapperMap.put(entityClass, mapperClass);
            }
        }
    }

    private void scanEntities() throws ClassNotFoundException {
        // Use FastClasspathScanner to scan the entity package for @Entity
        ScanResult results = new FastClasspathScanner("entity").scan();
        List<String> entityClasses = results.getNamesOfClassesWithAnnotation(Entity.class);

        for (String entityClassName : entityClasses) {
            Class<?> entityClass = Class.forName(entityClassName);
            if (entityClass.isAnnotationPresent(Entity.class)) {
                // Scan through all fields in the entity class
                int idCount = 0;
                for (Field field : entityClass.getDeclaredFields()) {
                    Column columnAnnotation = field.getAnnotation(Column.class);
                    if (columnAnnotation != null && columnAnnotation.id()) {
                        idCount++;
                    }
                }

                // Verify that only one field has id=true
                if (idCount != 1) {
                    throw new RuntimeException("Entity class " + entityClass.getName() +
                            " must have exactly one field with @Column(id=true), found " + idCount);
                }
            }
        }
    }

    public Object getMapper(Class<?> clazz) {
        // Create the proxy object for the mapper class supplied in clazz parameter
        // All proxies will use the supplied DaoInvocationHandler as the InvocationHandler
        Class<?> mapperClass = entityToMapperMap.get(clazz);
        if (mapperClass == null) {
            throw new IllegalArgumentException("No mapper found for entity class: " + clazz.getName());
        }

        return Proxy.newProxyInstance(
                mapperClass.getClassLoader(),
                new Class<?>[]{mapperClass},
                new DaoInvocationHandler()
        );
    }

    private void createTables() {
        // Go through all the Mapper classes in the map
        for (Map.Entry<Class, Class> entry : entityToMapperMap.entrySet()) {
            Class<?> mapperClass = entry.getValue();

            // Create a proxy instance for each
            Object mapperProxy = Proxy.newProxyInstance(
                    mapperClass.getClassLoader(),
                    new Class<?>[]{mapperClass},
                    new DaoInvocationHandler()
            );

            // Cast proxy to BasicMapper and invoke createTable()
            try {
                BasicMapper<?> basicMapper = (BasicMapper<?>) mapperProxy;
                basicMapper.createTable();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create table for mapper: " + mapperClass.getName(), e);
            }
        }
    }
}
