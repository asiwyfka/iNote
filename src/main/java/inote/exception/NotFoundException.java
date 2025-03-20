package inote.exception;

/**
 * Исключение, указывающее, что запрашиваемый клиентом ресурс не найден на сервере.
 * Код ошибки: 404.
 *
 * @author Avdeyev Viktor
 */
public class NotFoundException extends RuntimeException {
    /**
     * Конструктор исключения.
     *
     * @param message Сообщение.
     */
    public NotFoundException(final String message) {
        super(message);
    }
}
