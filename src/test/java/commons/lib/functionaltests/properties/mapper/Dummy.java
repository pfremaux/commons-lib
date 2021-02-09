package commons.lib.functionaltests.properties.mapper;

import java.util.Arrays;

public class Dummy {
    public final String[] elements;
    public final String name;
    public final Custom[] customs;

    public Dummy(String[] elements, String name, Custom[] customs) {
        this.elements = elements;
        this.name = name;
        this.customs = customs;
    }

    @Override
    public String toString() {
        return "Dummy{" +
                "elements=" + Arrays.toString(elements) +
                ", name='" + name + '\'' +
                ", customs=" + Arrays.toString(customs) +
                '}';
    }
}
