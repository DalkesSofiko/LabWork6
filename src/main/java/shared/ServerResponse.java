package shared;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * ответ, который сервер отправляет клиенту.
 */
public class ServerResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private Collection<?> data; // если нужно вернуть коллекцию обхектов

    public ServerResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = Collections.emptyList();
    }

    public ServerResponse(boolean success, String message, Collection<?> data) {
        this.success = success;
        this.message = message;
        this.data = data != null ? data : Collections.emptyList();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Collection<?> getData() {
        return data;
    }
}