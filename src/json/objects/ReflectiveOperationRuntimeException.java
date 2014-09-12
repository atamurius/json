package json.objects;

public class ReflectiveOperationRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReflectiveOperationRuntimeException(Throwable cause) {
		super(cause);
	}

	public ReflectiveOperationRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
