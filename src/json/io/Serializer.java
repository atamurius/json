package json.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Deque;
import java.util.LinkedList;

import json.Consumer;

public class Serializer implements Consumer {

	private Writer out;
	private String indent;
	
	public Serializer(Writer out) {
		this(out, null);
	}

	public Serializer(Writer out, String indent) {
		this.out = out;
		this.indent = indent;
	}

	protected void write(String value) {
		try {
			out.write(value);
		}
		catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	private void writeEscaped(String value) {
		write("\"");
		for (char c: value.toCharArray()) {
			if (c == '"' || c == '\\')
				write("\\");
			write(""+ c);
		}
		write("\"");
	}
	
	@Override
	public void onNull() {
		write("null");
	}

	@Override
	public void onString(String value) {
		writeEscaped(value);
	}

	@Override
	public void onNumber(Number value) {
		write(value.toString());
	}

	@Override
	public void onBoolean(boolean value) {
		write(String.valueOf(value));
	}

	private Deque<Integer> orderStack = new LinkedList<>();
	private int currentOrder = 0;
	private int level = 0;
	
	@Override
	public void beforeList() {
		write("[");
		orderStack.push(currentOrder);
		currentOrder = 0;
		level++;
	}
	
	@Override
	public void beforeListItem() {
		if (currentOrder++ > 0)
			write(", ");
	}

	@Override
	public void afterList() {
		write("]");
		currentOrder = orderStack.pop();
		level--;
	}
	
	@Override
	public void beforeObject() {
		write("{");
		orderStack.push(currentOrder);
		currentOrder = 0;
		level++;
	}

	@Override
	public void beforeProperty(String name) {
		if (currentOrder++ > 0)
			write(", ");
		indent();
		writeEscaped(name);
		write(": ");
	}

	protected void indent() {
		if (indent != null) {
			write("\n");
			for (int i = 0; i < level; i++)
				write(indent);
		}
	}

	@Override
	public void afterObject() {
		level--;
		if (currentOrder > 0)
			indent();
		write("}");
		currentOrder = orderStack.pop();
	}
	
	@Override
	public void afterListItem() { }
	
	@Override
	public void afterProperty(String name) { }
}
