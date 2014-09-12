package json;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public enum Type {

	NULL, STRING, NUMBER, BOOLEAN, LIST, OBJECT;
	
	public static Type maybeTypeOf(Object value) {
		if (value == null)
			return NULL;
		if (value instanceof String)
			return STRING;
		if (value instanceof Number)
			return NUMBER;
		if (value instanceof Boolean)
			return BOOLEAN;
		if (value instanceof List) {
			for (Object elem: (List<?>) value) {
				if (! (elem instanceof JsonValue))
					return null;
			}
			return LIST;
		}
		if (value instanceof Map) {
			for (Map.Entry<?, ?> entry : ((Map<?,?>) value).entrySet()) {
				if (! (entry.getValue() instanceof String))
					return null;
				if (! (entry.getValue() instanceof JsonValue))
					return null;
			}
			return OBJECT;
		}
		return null;
	}
	
	public static Type of(Object value) {
		Type t = maybeTypeOf(value);
		if (t == null)
			throw new IllegalArgumentException(value +" of "+ value.getClass() +" is not valid JSON value");
		else
			return t;
	}
	
	@SuppressWarnings("unchecked")
	String asStr(Object value) {
		switch (this) {
		case STRING:
		case NUMBER:
		case BOOLEAN:
			return value.toString();
		case NULL:
			return "null";
		case OBJECT:
			return "[object]";
		case LIST:
			StringBuilder sb = new StringBuilder();
			for (JsonValue elem: (List<JsonValue>) value) {
				sb.append(sb.length() == 0 ? "" : ",").append(elem.toString());
			}
			return sb.toString();
		default:
			throw new InternalError("Unimplemented");
		}
	}
	Number asNum(Object value) {
		switch (this) {
		case NULL:
			return 0;
		case NUMBER:
			return (Number) value;
		case BOOLEAN:
			return ((Boolean) value) ? 1 : 0;
		case STRING:
			if (value.toString().isEmpty())
				return 0;
			try {
				return new BigDecimal((String) value); 
			}
			catch (NumberFormatException e) {
				return Double.NaN;
			}
		case LIST:
			@SuppressWarnings("unchecked")
			List<JsonValue> values = (List<JsonValue>) value;
			return values.size() == 0 ? 0 : 
				values.size() == 1 ? values.get(0).toNumber() :
					Double.NaN;
		case OBJECT:
			return Double.NaN;
		default:
			throw new InternalError("Unimplemented");
		}
	}
	boolean asBool(Object value) {
		switch (this) {
		case NULL:
			return false;
		case BOOLEAN:
			return (Boolean) value;
		case STRING:
			return ! value.toString().isEmpty();
		case LIST:
		case OBJECT:
			return true;
		default:
			throw new InternalError("Unimplemented");
		}
	}
}
