package commons.lib.extra.server.http.handler.auth.pojo;

public class TestF {
    private final TestE z;
    private final TestE w;


    public TestF(TestE z, TestE w) {
        this.z = z;
        this.w = w;
    }

    @Override
    public String toString() {
        return "TestF{" +
                "z=" + z +
                ", w=" + w +
                '}';
    }
}