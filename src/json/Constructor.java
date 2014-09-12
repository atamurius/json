package json;

import java.util.Deque;
import java.util.LinkedList;

public class Constructor implements Consumer {

	private JsonValue current;
	private Deque<JsonValue> stack = new LinkedList<>(); 
	
	@Override
	public void onNull() {
		current = new JsonValue();
	}

	@Override
	public void onString(String value) {
		current = new JsonValue(value);
	}

	@Override
	public void onNumber(Number value) {
		current = new JsonValue(value);
	}

	@Override
	public void onBoolean(boolean value) {
		current = new JsonValue(value);
	}

	@Override
	public void beforeList() {
		stack.push(JsonValue.list());
	}

	@Override
	public void beforeListItem() { }

	@Override
	public void afterListItem() {
		stack.peek().toList().add(current);
	}

	@Override
	public void afterList() {
		current = stack.pop();
	}

	@Override
	public void beforeObject() {
		stack.push(JsonValue.object());
	}

	@Override
	public void beforeProperty(String name) { }

	@Override
	public void afterProperty(String name) {
		stack.peek().toObject().put(name, current);
	}

	@Override
	public void afterObject() {
		current = stack.pop();
	}

	public JsonValue toJson() {
		return current;
	}
	
	public static JsonValue consume(Producer producer) {
		Constructor constructor = new Constructor();
		producer.sendTo(constructor);
		return constructor.toJson();
	}
}
