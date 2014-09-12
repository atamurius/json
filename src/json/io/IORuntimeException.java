package json.io;

import java.io.IOException;

public class IORuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IORuntimeException(IOException cause) {
		super(cause);
	}
	
	public IOException getCause() {
		return (IOException) super.getCause();
	}
}
