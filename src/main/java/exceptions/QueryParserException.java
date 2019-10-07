package exceptions;

public class QueryParserException extends Exception {

	private static final long serialVersionUID = 1L;

	public QueryParserException(String message) {
        super(message);
    }

    public QueryParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
