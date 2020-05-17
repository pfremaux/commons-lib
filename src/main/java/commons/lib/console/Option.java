package commons.lib.console;

// TODO i18n
public class Option {
    private final String message;
    private final String value;
    private final Choosable choosable;

    public Option(String message, Choosable choosable) {
        this.message = message;
        this.value = null;
        this.choosable = choosable;
    }

    public Option(String message, String value) {
        this.message = message;
        this.value = value;
        this.choosable = null;
    }


    public Choosable getChoosable() {
        return choosable;
    }

    public String getMessage() {
        return message;
    }

    public String getValue() {
        return value;
    }
}
