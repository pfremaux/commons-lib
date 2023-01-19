package commons.lib.extra.server.http.handler.auth.pojo;

public class TestE {
    private final int a;
    private final String b;

    public TestE(int a, String b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return "TestE{" +
                "a='" + a + '\'' +
                ", b='" + b + '\'' +
                '}';
    }
}