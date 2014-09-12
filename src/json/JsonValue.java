package json;

import static java.util.Arrays.asList;
import static json.Type.LIST;
import static json.Type.OBJECT;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON Value, mutable, can be one of {@link Type}:
 * - null
 * - boolean (true/false)
 * - number
 * - string
 * - list of JSON values
 * - object as map with string keys and JSON values
 */
public class JsonValue implements Producer {

	private Type type;
	private Object value;
	
	/**
	 * Creates empty (null) value
	 */
	public JsonValue() {
		withValue(null);
	}
	
	/**
	 * Creates string (or null) value
	 */
	public JsonValue(String value) {
		set(value);
	}
	/**
	 * Creates number (or null) value
	 */
	public JsonValue(Number value) {
		set(value);
	}
	/**
	 * Creates boolean value
	 */
	public JsonValue(boolean value) {
		set(value);
	}
	/**
	 * Creates list value (even in case of 1 or 0 parameters)
	 */
	public JsonValue(JsonValue... values) {
		set(values);
	}
	
	/**
	 * Creates new empty list value
	 */
	public static JsonValue list() {
		JsonValue value = new JsonValue();
		value.toList();
		return value;
	}
	
	/**
	 * Creates new empty object value
	 */
	public static JsonValue object() {
		JsonValue value = new JsonValue();
		value.toObject();
		return value;
	}

	/**
	 * Set null value
	 */
	public void setNull() {
		withValue(null);
	}
	/**
	 * Set string (or null) value
	 */
	public void set(String value) {
		withValue(value);
	}
	/**
	 * Set number (or null) value
	 */
	public void set(Number value) {
		withValue(value);
	}
	/**
	 * Set boolean value
	 */
	public void set(boolean value) {
		withValue(value);
	}
	/**
	 * Set list value
	 */
	public void set(JsonValue... values) {
		withValue(new ArrayList<>(asList(values)));
	}
	
	public Type type() {
		return type;
	}
	
	/**
	 * Gets raw value: null, String, Number, List or Map
	 * use to* methods to get value of specified type
	 */
	public Object value() {
		return value;
	}
	
	public String toString() {
		return type.asStr(value);
	}
	
	public Number toNumber() {
		return type.asNum(value);
	}
	
	public boolean toBoolean() {
		return type.asBool(value);
	}
	
	/**
	 * Returns list of values (in case if value was not list it is converted to list and old value is discarded)
	 */
	@SuppressWarnings("unchecked")
	public List<JsonValue> toList() {
		if (type != LIST) {
			withValue(new ArrayList<>());
		}
		return (List<JsonValue>) value;
	}
	
	/**
	 * Returns map of values (in case if value was not object it is converted to object and old value is discarded)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, JsonValue> toObject() {
		if (type != OBJECT) {
			withValue(new LinkedHashMap<>());
		}
		return (Map<String, JsonValue>) value;
	}
	
	public JsonValue with(String name, JsonValue value) {
		this.toObject().put(name, value);
		return this;
	}
	public JsonValue with(JsonValue value) {
		this.toList().add(value);
		return this;
	}
	public JsonValue withNull(String name) {
		return this.with(name, new JsonValue());
	}
	public JsonValue with(String name, String value) {
		return this.with(name, new JsonValue(value));
	}
	public JsonValue with(String name, Number value) {
		return this.with(name, new JsonValue(value));
	}
	public JsonValue with(String name, boolean value) {
		return this.with(name, new JsonValue(value));
	}
	public JsonValue with(String name, JsonValue... values) {
		return this.with(name, new JsonValue(values));
	}
	public JsonValue withNull() {
		return this.with(new JsonValue());
	}
	public JsonValue with(String value) {
		return this.with(new JsonValue(value));
	}
	public JsonValue with(Number value) {
		return this.with(new JsonValue(value));
	}
	public JsonValue with(boolean value) {
		return this.with(new JsonValue(value));
	}
	public JsonValue with(JsonValue... values) {
		return this.with(new JsonValue(values));
	}
	
	public JsonValue get(int index) {
		return toList().get(index);
	}
	
	public JsonValue get(String prop) {
		return toObject().get(prop);
	}
	
	/**
	 * Returns size of list or 0 if value is not list
	 */
	public int size() {
		if (type == Type.LIST)
			return toList().size();
		else
			return 0;
	}
	
	/**
	 * Sends value (recursively) to consumer
	 */
	@Override
	public void sendTo(Consumer consumer) {
		switch (type) {
		case NULL:
			consumer.onNull();
			break;
		case BOOLEAN:
			consumer.onBoolean(toBoolean());
			break;
		case STRING:
			consumer.onString(toString());
			break;
		case NUMBER:
			consumer.onNumber(toNumber());
			break;
		case LIST:
			consumer.beforeList();
			for (JsonValue element: toList()) {
				consumer.beforeListItem();
				element.sendTo(consumer);
				consumer.afterListItem();
			}
			consumer.afterList();
			break;
		case OBJECT:
			consumer.beforeObject();
			for (String property: toObject().keySet()) {
				consumer.beforeProperty(property);
				get(property).sendTo(consumer);
				consumer.afterProperty(property);
			}
			consumer.afterObject();
			break;
		}
	}

	@Override
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JsonValue) {
			JsonValue that = (JsonValue) obj;
			if (this.value == null && that.value == null)
				return true;
			return this.value != null && this.value.equals(that.value);
		}
		else
			return false;
	}

	private JsonValue withValue(Object value) {
		this.type = Type.of(value);
		this.value = value;
		return this;
	}
}












