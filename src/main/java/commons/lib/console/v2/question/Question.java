package commons.lib.console.v2.question;

import commons.lib.console.v2.Console;

public class Question {
    private final String msg;

    public Question(String msg) {
        this.msg = msg;
    }

    public String ask() {
        Console.get().printf(msg);
        return Console.get().readLine();
    }

    private int getInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return -1;
        }
    }

    public String getMsg() {
        return msg;
    }

}
