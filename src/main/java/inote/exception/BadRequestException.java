package inote.exception;

/**
 * Исключение, указывающее на неправильный запрос клиента.
 * Код ошибки: 400.
 *
 * @author Avdeyev Viktor
 **/
public class BadRequestException extends RuntimeException {
    /**
     * Конструктор исключения.
     *
     * @param message Сообщение.
     */
    public BadRequestException(final String message) {
        super(message);
    }
}
