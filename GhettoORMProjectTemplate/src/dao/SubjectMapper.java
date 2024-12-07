package dao;

import java.util.List;

import annotations.CreateTable;
import annotations.Delete;
import annotations.MappedClass;
import annotations.Param;
import annotations.Save;
import annotations.Select;
import entity.Student;
import entity.Subject;

@MappedClass(clazz=Subject.class)
public interface SubjectMapper extends BasicMapper<Subject>  // all mappers should extend BasicMapper
{

	@CreateTable
	public void createTable();	
	
	@Save
	public void save(Subject s);	
	
	@Delete
	public void delete(Subject s);	
	
	
	// you may add more @Select here for testing
}

