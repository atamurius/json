package json;

import static json.JsonValue.list;
import static json.JsonValue.object;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.text.ParseException;

import json.io.Deserializer;
import json.io.JsonIO;

import org.junit.Before;
import org.junit.Test;

public class DeserializerTest {

	@Before
	public void setUp() {
		JsonIO.parser = Deserializer.PARSER;
	}
	
	@Test
	public void testDeserializer() throws ParseException {
		assertValuesEquals(
				object()
					.with("name","Alex \"Any Key\" \\ Here")
					.with("age", BigInteger.valueOf(12))
					.withNull("empty")
					.with("married", false)
					.with("male", true)
					.with("marks", list().with("1").with("2").with("3"))
				, 
				Constructor.consume(JsonIO.parse(
				"{\n"+
				"   \"name\": \"Alex \\\"Any Key\\\" \\\\ Here\","+
				"   \"age\": 12,"+
				"   \"empty\": null,"+
				"   \"married\": false,"+
				"   \"male\": true,"+
				"   \"marks\": [ \"1\", \"2\",  \"3\"]"+
				"}"))
		);
	}

	@Test
	public void testTokDeserializer() throws ParseException {
		assertValuesEquals(
				object()
					.with("name","Alex \"Any Key\" \\ Here")
					.with("age", 12d)
					.withNull("empty")
					.with("married", false)
					.with("male", true)
					.with("marks", list().with("1").with("2").with("3"))
				, 
				Constructor.consume(JsonIO.parse(
				"{\n"+
				"   \"name\": \"Alex \\\"Any Key\\\" \\\\ Here\","+
				"   \"age\": 12,"+
				"   \"empty\": null,"+
				"   \"married\": false,"+
				"   \"male\": true,"+
				"   \"marks\": [ \"1\", \"2\",  \"3\"]"+
				"}"))
		);
	}

	private void assertValuesEquals(JsonValue exp, JsonValue act) {
		assertEquals(exp +" ~ "+ act, exp.type(), act.type());
		switch (exp.type()) {
		case LIST:
			assertEquals(exp.size(), act.size());
			for (int i = 0; i < exp.size(); i++)
				assertValuesEquals(exp.get(i), act.get(i));
			break;
		case OBJECT:
			assertEquals(exp.toObject().size(), act.toObject().size());
			for (String key: exp.toObject().keySet()) {
				assertValuesEquals(exp.get(key), act.get(key));
			}
			break;
		default:
			assertEquals(exp.value(), act.value());
		}
	}
}







