package json.io;

import static java.io.StreamTokenizer.TT_EOF;
import static java.io.StreamTokenizer.TT_NUMBER;
import static java.io.StreamTokenizer.TT_WORD;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

import json.Consumer;
import json.Producer;

public class TokenizedDeserializer implements Producer {

	private StreamTokenizer in;
	private int pos;

	public static final Parser PARSER = new Parser() {
		public Producer parse(Reader in) {
			return new TokenizedDeserializer(in);
		}
	};
	
	public TokenizedDeserializer(final Reader reader) {
		// wrapper for current position tracking
		in = new StreamTokenizer(new Reader() {
			@Override
			public int read(char[] cbuf, int off, int len) throws IOException {
				int cnt = reader.read(cbuf, off, len);
				pos += cnt;
				return cnt;
			}
			@Override
			public int read() throws IOException {
				pos++;
				return reader.read();
			}
			
			@Override
			public void close() throws IOException {
				reader.close();
			}
		});
		
		in.resetSyntax();
		in.wordChars('a', 'z');
        in.whitespaceChars(0, ' ');
        in.quoteChar('"');
        in.parseNumbers();
	}
	
	private int next() {
		try {
			int next = in.nextToken();
			if (next == TT_EOF)
				throw new EOFException();
			else
				return next;
		}
		catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}
	
	@Override
	public void sendTo(Consumer consumer) {
		switch (next()) {
		case TT_WORD:
			switch (in.sval) {
			case "null":
				consumer.onNull();
				break;
			case "true":
			case "false":
				consumer.onBoolean("true".equals(in.sval));
				break;
			default:
				fail("Unexpected word: "+ in.sval);
			}
			break;
		case TT_NUMBER:
			consumer.onNumber(in.nval);
			break;
		case '"':
			consumer.onString(in.sval);
			break;
		case '{':
			readObjectTo(consumer);
			break;
		case '[':
			readListTo(consumer);
			break;
		default:
			fail("Unexpected token: "+ in.ttype +"("+ ((char) in.ttype) +")");
		}
	}
	
	private void readListTo(Consumer consumer) {
		consumer.beforeList();
		if (next() != ']') {
			in.pushBack();
			do {
				consumer.beforeListItem();
				sendTo(consumer);
				consumer.afterListItem();
			}
			while (next() == ',');
			if (in.ttype != ']')
				fail("']' expected at the end of list");
		}
		consumer.afterList();
	}

	private void readObjectTo(Consumer consumer) {
		consumer.beforeObject();
		if (next() != '}') {
			in.pushBack();
			do {
				if (next() != '"')
					fail("String property name expected");
				String property = in.sval;
				if (next() != ':')
					fail("':' expected after property name");
				consumer.beforeProperty(property);
				sendTo(consumer);
				consumer.afterProperty(property);
			}
			while (next() == ',');
			if (in.ttype != '}')
				fail("'}' expected at the end of object");
		}
		consumer.afterObject();
	}
	
	private void fail(String expected) throws ParseException {
		throw new ParseException(expected, pos);
	}	
}




