package commons.lib.tooling.java.v1;

import commons.lib.tooling.java.v1.code.JavaFile;
import commons.lib.tooling.java.v1.code.ReferenceClass;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProjectGenerator {
    private static final Map<String, ReferenceClass> BASE_CLASSES = new HashMap<>(Map.of(
            "Integer", new ReferenceClass("java.lang", "Integer")
    ));
    public static final Map<String, ReferenceClass> ALL_CLASSES = new HashMap<>(BASE_CLASSES);
    private final List<JavaFile> files = new ArrayList<>();
    private final Map<String, Object> tmp = new HashMap<>();

    public JavaFile newClassFile(String packageLocation, String name) {
        final JavaFile javaFile = new JavaFile(packageLocation, name, null);
        files.add(javaFile);
        // There is a risk of conflict if some classes are the same. But for now it's easier

        ALL_CLASSES.put(javaFile.getReferenceClass().getClassName(), javaFile.getReferenceClass());
        return javaFile;
    }

    public static void referenceAllClasses(Set<String> classesPaths) {
        final Map<String, ReferenceClass> map = classesPaths.stream().map(ReferenceClass::fromExistingClass).collect(Collectors.toMap(ReferenceClass::getClassName, Function.identity()));
        ALL_CLASSES.putAll(map);
    }

    public JavaFile newPojo(String pack, String name, Map<String, String> attributes) {
        final JavaFile pojo = JavaFile.pojoOf(pack, name, attributes);
        files.add(pojo);
        ALL_CLASSES.put(pojo.getReferenceClass().getClassName(), pojo.getReferenceClass());
        return pojo;
    }

    public List<JavaFile> getFiles() {
        return files;
    }

    public <T> T s(String alias, T v) {
        tmp.put(alias, v);
        return v;
    }

}
