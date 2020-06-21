package commons.lib.console.v2;

import java.util.UUID;

public class Choice {

    private final UUID uuid;
    private final String id;
    private final String name;

    public Choice(String id, String name) {
        this.uuid = UUID.randomUUID();
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public static class Predefined {

        public static final Choice CANCEL_ACTION = new Choice("cancel", "cancel");
        public static final Choice SAVE_ACTION = new Choice("save", "save");

        public static final Choice MAIN = new Choice("main", "main");
        public static final Choice ON_FAIL = new Choice("onfail", "on fail");
    }
}
