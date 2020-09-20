package commons.lib.test.provider;

import java.util.ServiceLoader;

public class MainTest {

    public static void main(String[] args) {
        final MainTest test = new MainTest();
        Contract c = test.getInstance(Contract.class);
        System.out.println(c.getSomething());
    }

    public <T> T getInstance(Class<T> tClass) {
        return ServiceLoader.load(tClass).findFirst().orElse(null);
    }
}
