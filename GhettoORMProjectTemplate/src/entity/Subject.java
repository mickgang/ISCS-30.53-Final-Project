package entity;

import annotations.Column;
import annotations.Entity;

@Entity(table="subject")
public class Subject {
	
	@Column(name="id", 			sqlType="INTEGER not NULL AUTO_INCREMENT", id=true)
	private Integer id;

	@Column(name="name", 	sqlType="VARCHAR(255)")
	private String name;

	@Column(name="num_students", 		 sqlType="INTEGER")
	private Integer numStudents;

	
	
	// make all the getter/setter/toString
}
