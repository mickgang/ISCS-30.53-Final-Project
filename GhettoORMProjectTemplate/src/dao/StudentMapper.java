package dao;

import java.util.List;

import annotations.CreateTable;
import annotations.Delete;
import annotations.MappedClass;
import annotations.Param;
import annotations.Save;
import annotations.Select;
import entity.Student;

@MappedClass(clazz=Student.class)
public interface StudentMapper extends BasicMapper<Student>  // all mappers should extend BasicMapper with the correct type
{

	@CreateTable
	public void createTable();	
	
	@Save
	public void save(Student s);	
	
	@Delete
	public void delete(Student s);	
	
		
	//note: always double check SQL that it works
	//		always check if the thing being inserted is supposed to be a string, don't forget quotes
		
	@Select("select * from :table where pk = :id")   
	public Student getById(@Param("id") Integer id);
	
	
	@Select("select * from :table")
	public List<Student> getAll();
	
	// WARNING: the query itself should no longer have the string quotes
	// these are to be added based on the parameter type
	@Select("select * from :table where first_name = :firstName and last_name = :lastName")
	public Student getByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName );

}

