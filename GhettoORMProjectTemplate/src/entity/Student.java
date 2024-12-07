package entity;

import annotations.Column;
import annotations.Entity;

@Entity(table="student")
public class Student {
	
	@Column(name="pk", 			sqlType="INTEGER not NULL AUTO_INCREMENT", id=true)
	private Integer id;

	@Column(name="first_name", 	sqlType="VARCHAR(255)")
	private String first;

	@Column(name="last_name", 	sqlType="VARCHAR(255)")
	private String last;

	@Column(name="age", 		sqlType="INTEGER")
	private Integer age;

	
	// make all the getter/setter/toString

}
