package dao;

// DO NOT DO ANYTHING WITH THIS CLASS

public interface BasicMapper<T> {
	
	// this allows all Mapper classes to be treated in a common way	
	public void createTable();	
	public void save(T s);	
	public void delete(T s);	
}
