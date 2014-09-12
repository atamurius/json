package json;

import static json.JsonValue.list;
import static json.JsonValue.object;
import static org.junit.Assert.assertEquals;
import json.io.JsonIO;

import org.junit.Test;

public class SerializerTest {

	@Test
	public void test() {
		JsonValue value = list()
				.withNull()
				.with(true)
				.with(false)
				.with("String \"with\" some \\ slash")
				.with(10)
				.with(3.14)
				.with(list().with(1).with(2).with(3))
				.with(object()
						.with("key1", 10)
						.with("key2", list().with(1).with(2).with(3)));
		
		assertEquals(
				"[null, true, false, \"String \\\"with\\\" some \\\\ slash\", 10, 3.14, [1, 2, 3], {\"key1\": 10, \"key2\": [1, 2, 3]}]", 
				JsonIO.toString(value));
	}

}
