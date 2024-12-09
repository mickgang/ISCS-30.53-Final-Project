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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNumStudents() {
		return numStudents;
	}
	public void setNumStudents(int numStudents) {
		this.numStudents = numStudents;
	}
	@Override
	public String toString() {
		return String.format("Subject [id=%s, firstName=%s, lastName=%s]", id, name, numStudents);
	}
}
