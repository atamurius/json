package json;

import static org.junit.Assert.*;
import static json.JsonValue.*;
import org.junit.Test;

public class JsonValueTest {

	@Test
	public void test() {
		JsonValue student = object()
				.with("name", "Alex")
				.with("age", 21)
				.with("married", false)
				.with("marks",
					object().with("title", "Math")     .with("mark", 5),
					object().with("title", "Chemistry").with("mark", 4)
				);
		
		assertEquals("Alex", student.get("name").toString());
		assertEquals(21, student.get("age").toNumber());
		assertFalse(student.get("married").toBoolean());
		assertEquals(2, student.get("marks").size());
		assertEquals("Math", student.get("marks").get(0).get("title").toString());
		assertEquals(5, student.get("marks").get(0).get("mark").toNumber());
		assertEquals("Chemistry", student.get("marks").get(1).get("title").toString());
		assertEquals(4, student.get("marks").get(1).get("mark").toNumber());
	}

}
