package json.objects;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public class Student {
	
	private String name;
	private int age;
	private boolean isGoodStudent;
	
	private List<Mark> marks = new ArrayList<>();
	
	public Student(String name, int age, boolean isGoodStudent, Mark... marks) {
		this.name = name;
		this.age = age;
		this.isGoodStudent = isGoodStudent;
		this.marks.addAll(asList(marks));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isGoodStudent() {
		return isGoodStudent;
	}

	public void setGoodStudent(boolean isGoodStudent) {
		this.isGoodStudent = isGoodStudent;
	}
	
	public List<Mark> getMarks() {
		return marks;
	}
}