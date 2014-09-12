package json.io;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import json.Producer;

public class JsonIO {

	public static Parser parser = Deserializer.PARSER;

	public static Producer parse(String str) {
		return new Deserializer(new StringReader(str));
	}

	public static String toString(Producer producer) {
		return toString(producer, null);
	}

	public static String toString(Producer producer, String indent) {
		StringWriter out = new StringWriter();
		producer.sendTo(new Serializer(out, indent));
		return out.toString();
	}

	public static final Serializer STDOUT;
	static {
		final Writer out = new OutputStreamWriter(System.out);
		STDOUT = new Serializer(out, "  ") {
			protected void write(String value) {
				try {
					out.write(value);
					out.flush();
				}
				catch (IOException e) {
					throw new IORuntimeException(e);
				}
			}
		};
	}
}
