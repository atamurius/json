package json.io;

public class ParseException extends RuntimeException {

	private int offset;

	public ParseException(String string, int i) {
		super(string);
		this.offset = i;
	}

	private static final long serialVersionUID = 1L;

	public int getOffset() {
		return offset;
	}

	public java.text.ParseException toChecked() {
		return (java.text.ParseException) new java.text.ParseException(getMessage(), getOffset()).initCause(this);
	}
}
