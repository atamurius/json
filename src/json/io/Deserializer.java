package json.io;

import static java.lang.Character.isWhitespace;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

import json.Consumer;
import json.Producer;

public class Deserializer implements Producer {

	private Reader in;
	private int pos = -1;

	public static final Parser PARSER = new Parser() {
		public Producer parse(Reader in) {
			return new Deserializer(in);
		}
	};
	
	public Deserializer(Reader in) {
		this.in = in;
	}
	
	protected int next() {
		int next;
		do {
			next = rawNext();
		}
		while (next != -1 && isWhitespace((char) next));
		return next;
	}
	
	protected int rawNext() {
		try {
			int next = in.read();
			pos++;
			return next;
		}
		catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}
	
	private int readNextTo(int next, Consumer consumer) {
		switch(next) {
		case '{':
			return readObjectTo(consumer);
		case '[':
			return readListTo(consumer);
		case '"':
			consumer.onString(readString());
			return next();
		case 'n':
			ensure("null");
			consumer.onNull();
			return next();
		case 't':
			ensure("true");
			consumer.onBoolean(true);
			return next();
		case 'f':
			ensure("false");
			consumer.onBoolean(false);
			return next();
		default:
			return readNumberTo(next, consumer);
		}
	}

	private int readNumberTo(int next, Consumer consumer) {
		int pos = this.pos;
		StringBuilder buff = new StringBuilder();
		while (next != -1 && "-1234567890.".indexOf((char) next) != -1) {
			buff.append((char) next);
			next = rawNext();
		}
		consumer.onNumber(toNumber(buff.toString(), pos));
		if (isWhitespace((char) next))
			next = next();
		return next;
	}

	private int readListTo(Consumer consumer) {
		int next;
		consumer.beforeList();
		do {
			next = next();
			if (next != ']') {
				consumer.beforeListItem();
				next = readNextTo(next, consumer);
				consumer.afterListItem();
			}
		}
		while (next == ',');
		ensure(next,']');
		consumer.afterList();
		return next();
	}

	private int readObjectTo(Consumer consumer) {
		int next;
		consumer.beforeObject();
		do {
			next = next();
			if (next == '"') {
				String name = readString();
				consumer.beforeProperty(name);
				ensure(next(),':');
				next = readNextTo(next(), consumer);
				consumer.afterProperty(name);
			}
		}
		while (next == ',');
		ensure(next,'}');
		consumer.afterObject();
		return next();
	}
	
	@Override
	public void sendTo(Consumer consumer) {
		readNextTo(next(), consumer);
	}
	
	private Number toNumber(String string, int pos) {
		try {
			if (string.contains("."))
				return new BigDecimal(string);
			else
				return new BigInteger(string);
		}
		catch (NumberFormatException e) {
			throw new ParseException("Number expected, but got '"+ string +"'", pos);
		}
	}

	private void ensure(String str) {
		int pos = this.pos - 1;
		for (int i = 1; i < str.length(); i++) {
			if (rawNext() != str.charAt(i))
				throw new ParseException("'"+ str +"' expected", pos);
		}
	}

	private String readString() {
		StringBuilder buff = new StringBuilder();
		int next;
		while ((next = rawNext()) != '"') {
			if (next == '\\')
				next = rawNext();
			buff.append(toChar(next));
		}
		return buff.toString();
	}

	private char toChar(int next) {
		if (next == -1)
			throw new ParseException("Unexpected end of data", pos);
		else
			return (char) next;
	}

	private void ensure(int next, char exp) {
		if (next != exp)
			throw new ParseException("'"+ exp +"' expected", pos);
	}
}
