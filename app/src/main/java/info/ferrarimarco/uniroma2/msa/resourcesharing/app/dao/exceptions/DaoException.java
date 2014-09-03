package info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.exceptions;


public class DaoException extends RuntimeException {

    /**
     * Constructs a new {@code DaoException} that includes the current stack
     * trace.
     */
    public DaoException() {
        super();
    }

    /**
     * Constructs a new {@code DaoException} with the current stack trace
     * and the specified detail message.
     *
     * @param detailMessage the detail message for this exception.
     */
    public DaoException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new {@code DaoException} with the current stack trace
     * and the specified cause.
     *
     * @param throwable the cause of this exception.
     */
    public DaoException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructs a new {@code DaoException} with the current stack trace,
     * the specified detail message and the specified cause.
     *
     * @param detailMessage the detail message for this exception.
     * @param throwable     root cause of this exception
     */
    public DaoException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
