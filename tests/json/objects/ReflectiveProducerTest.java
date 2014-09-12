package json.objects;

import static java.util.Arrays.asList;
import static json.io.JsonIO.STDOUT;
import static json.objects.ReflectiveProducer.reflect;

import java.util.Collection;

import json.io.JsonIO;

import org.junit.Test;

public class ReflectiveProducerTest {

	Collection<Student> students = asList(
			new Student("Alex", 18, false, 
					new Mark("Math", 3),
					new Mark("Bio", 4)),
			new Student("Mary", 19, true,
					new Mark("Math", 4),
					new Mark("Bio", 5))
			);
	
	@Test
	public void test1() {
		reflect(students).sendTo(STDOUT);
	}

	@Test
	public void test2() {
		String string = JsonIO.toString(reflect(students));
		System.out.println();
		System.out.println(string);
		System.out.println();
		JsonIO.parse(string).sendTo(STDOUT);
	}
}
