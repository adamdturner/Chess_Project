package results;

public class ErrorResult extends Result {
    private String message;

    public ErrorResult(String message) {
        this.message = message;
    }

    // Getter and setter for message...

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

