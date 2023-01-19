package commons.lib.extra.server.http.handler.testLib;

import java.util.List;

public class TestArray {
    private final List<String> s;
    private final List<Double> n;
    private final List<Body> o;

    public TestArray(List<String> s, List<Double> n, List<Body> o) {
        this.s = s;
        this.n = n;
        this.o = o;
    }

    public List<Body> getO() {
        return o;
    }

    public List<String> getS() {
        return s;
    }

    public List<Double> getN() {
        return n;
    }

    @Override
    public String toString() {
        return "TestArray{" +
                "s=" + s +
                ", n=" + n +
                '}';
    }
}
