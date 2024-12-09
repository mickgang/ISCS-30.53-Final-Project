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
	public String getFirst() {
		return first;
	}
	public void setFirst(String firstName) {
		this.first = firstName;
	}
	public String getLast() {
		return last;
	}
	public void setLast(String lastName) {
		this.last = lastName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	@Override
	public String toString() {
		return String.format("Student [id=%s, firstName=%s, lastName=%s]", id, first, last);
	}

}
