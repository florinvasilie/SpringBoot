package app.server.model;

/**
 * Created by fvasilie on 9/19/2016.
 */
public class Greeting {
    private final Long id;
    private final String message;

    public Greeting(Long id, String message) {
        this.id = id;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
