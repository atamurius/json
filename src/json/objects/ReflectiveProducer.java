package json.objects;

import static java.lang.Character.toLowerCase;
import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import json.Consumer;
import json.Producer;

public class ReflectiveProducer implements Producer {

	private Object obj;
	
	public ReflectiveProducer(Object obj) {
		this.obj = obj;
	}
	
	public static ReflectiveProducer reflect(Object obj) {
		return new ReflectiveProducer(obj);
	}

	@Override
	public void sendTo(Consumer consumer) {
		send(obj, consumer);
	}
	
	public void send(Object obj, Consumer consumer) {
		if (obj == null) {
			consumer.onNull();
		}
		else if (obj instanceof String) {
			consumer.onString((String) obj);
		}
		else if (obj instanceof Boolean) {
			consumer.onBoolean((boolean) obj);
		}
		else if (obj instanceof Number) {
			consumer.onNumber((Number) obj);
		}
		else if (obj instanceof Map) {
			sendMap((Map<?,?>) obj, consumer);
		}
		else if (obj instanceof Collection) {
			sendList((Collection<?>) obj, consumer);
		}
		else {
			sendObject(obj, consumer);
		}
	}

	private void sendObject(Object obj, Consumer consumer) {
		consumer.beforeObject();
		for (Method method: obj.getClass().getMethods()) {
			if (! isStatic(method.getModifiers()) 
					&& method.getDeclaringClass() != Object.class 
					&& isGetter(method)) {
				String prop = getterToProperty(method.getName());
				consumer.beforeProperty(prop);
				send(invoke(obj, method), consumer);
				consumer.afterProperty(prop);
			}
		}
		consumer.afterObject();
	}

	private Object invoke(Object obj, Method method) {
		try {
			return method.invoke(obj);
		}
		catch (InvocationTargetException e) {
			throw new InvocationTargetRuntimeException(e.getTargetException());
		}
		catch (ReflectiveOperationException e) {
			throw new ReflectiveOperationRuntimeException(e);
		}
	}

	private String getterToProperty(String name) {
		name = name.substring(name.startsWith("get") ? 3 : 0);
		return toLowerCase(name.charAt(0)) + name.substring(1);
	}

	private boolean isGetter(Method method) {
		String name = method.getName();
		return (name.startsWith("get") || name.startsWith("is")) && 
				method.getParameterTypes().length == 0 &&
				method.getReturnType() != Void.TYPE;
	}

	private void sendList(Collection<?> obj, Consumer consumer) {
		consumer.beforeList();
		for (Object element: obj) {
			consumer.beforeListItem();
			send(element, consumer);
			consumer.afterListItem();
		}
		consumer.afterList();
	}

	private void sendMap(Map<?, ?> obj, Consumer consumer) {
		consumer.beforeObject();
		for (Object key: obj.keySet()) {
			String stringKey = key.toString();
			consumer.beforeProperty(stringKey);
			send(obj.get(key), consumer);
			consumer.afterProperty(stringKey);
		}
		consumer.afterObject();
	}
}
