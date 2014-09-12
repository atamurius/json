package json.objects;

import java.awt.List;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import json.Consumer;

// TODO
public class ReflectiveConsumer<T> implements Consumer {

	private Class<T> type;
	private T result;

	private static final Map<Class<?>, Class<?>> COLLECTION_INSTANCES = new HashMap<>();
	static {
		COLLECTION_INSTANCES.put(List.class, ArrayList.class);
		COLLECTION_INSTANCES.put(Set.class, HashSet.class);
		COLLECTION_INSTANCES.put(SortedSet.class, TreeSet.class);
		COLLECTION_INSTANCES.put(Map.class, HashMap.class);
		COLLECTION_INSTANCES.put(SortedMap.class, TreeMap.class);
		COLLECTION_INSTANCES.put(Collection.class, ArrayList.class);
	}
	
	public ReflectiveConsumer(Class<T> type) {
		this.type = type;
	}

	public static <T> ReflectiveConsumer<T> consumerOf(Class<T> type) {
		return new ReflectiveConsumer<T>(type);
	}
	
	public T getResult() {
		return result;
	}

	@Override
	public void onNull() {
		result = null;
	}

	@Override
	public void onString(String value) {
		result = type.cast(value);
	}

	@Override
	public void onNumber(Number value) {
		if (! Number.class.isAssignableFrom(type))
			throw new ClassCastException(value.getClass() +" got where "+ this.type +" is expected");
		if (type.isAssignableFrom(Float.TYPE))
			result = type.cast(value.doubleValue());
		else
			result = type.cast(value.longValue());
	}

	@Override
	public void onBoolean(boolean value) {
		result = type.cast(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void beforeList() {
		if (! Collection.class.isAssignableFrom(type))
			throw new ClassCastException("List got where "+ this.type +" is expected");
		ReflectiveConsumer<T> elementConsumer = consumerOf(type.getTypeParameters()[0].getGenericDeclaration());
		if (type.isInterface() && COLLECTION_INSTANCES.containsKey(type)) {
			type = (Class<T>) COLLECTION_INSTANCES.get(type);
		}
		Collection<?> results = (Collection<?>) createInstance();
	}

	private T createInstance() {
		try {
			return type.getConstructor().newInstance();
		}
		catch (InvocationTargetException e) {
			throw new InvocationTargetRuntimeException(e);
		}
		catch (ReflectiveOperationException e) {
			throw new ReflectiveOperationRuntimeException(e);
		}
	}

	@Override
	public void beforeListItem() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterListItem() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterList() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeObject() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeProperty(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterProperty(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterObject() {
		// TODO Auto-generated method stub
		
	}
	
	
}
