package pianissimo.jflowty.unions;

public class UnionTypeException extends RuntimeException {
	public UnionTypeException (String message) {
		super(message);
	}

	public UnionTypeException (Throwable cause) {
		super(cause);
	}

	public UnionTypeException (String message, Throwable cause) {super(message, cause);}

	public UnionTypeException (String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnionTypeException (Attempt attempt) {
		super(String.format("Attempt is of the wrong type: %s", attempt));
	}

	public UnionTypeException (Either either) {
		super(String.format("Either is of the wrong type: %s", either));
	}

	public UnionTypeException (Option option) {
		super(String.format("Option is of the wrong type: %s", option));
	}
}
