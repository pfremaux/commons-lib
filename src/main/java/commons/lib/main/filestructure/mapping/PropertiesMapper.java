package commons.lib.main.filestructure.mapping;

import commons.lib.main.AnnotationUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PropertiesMapper {
    public static Map<String, Class<?>> types = new HashMap<>();
    List<PartialPropertyName> partialPropertyNames = new ArrayList<>();

    public void initMapper(String rootPackage) throws IOException, ClassNotFoundException {
        partialPropertyNames.addAll(mapAllIn(rootPackage));
    }

    public List loadFromPropertiesFiles() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchFieldException {
        List result = new ArrayList();
        for (PartialPropertyName partialPropertyName : partialPropertyNames) {
            Object o = mapAllToClass(null, partialPropertyName, types.get(partialPropertyName.getName()));
            if (o != null) {
                result.add(o);
            }
        }
        System.out.println(result);
        return result;
    }

    public StringBuilder getPropertiesExample() {
        final List<String> all = new ArrayList<>();
        for (PartialPropertyName partialPropertyName : partialPropertyNames) {
            all.addAll(getAllExampleOfAllKeys(null, partialPropertyName));
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < all.size(); i++) {
            builder.append(all.get(i)).append("=lorem ispum").append(i).append("\n");
        }
        return builder;
    }


    private List<String> getExampleOfAllKeys(String prefix, List<PartialPropertyName> partialPropertyNames) {
        final List<String> result = new ArrayList<>();
        for (PartialPropertyName partialPropertyName : partialPropertyNames) {
            result.addAll(getAllExampleOfAllKeys(prefix, partialPropertyName));
        }
        return result;
    }

    private List<String> getAllExampleOfAllKeys(String prefix, PartialPropertyName partialPropertyName) {
        if (partialPropertyName.getAttributes() != null) {
            if (partialPropertyName.isArray()) {
                if (partialPropertyName.getAttributes().isEmpty()) {
                    return Arrays.asList(
                            getKey(getKey(prefix, partialPropertyName.getName()), "0"),
                            getKey(getKey(prefix, partialPropertyName.getName()), "1")
                    );
                } else {
                    final List<String> result = new ArrayList<>();
                    result.addAll(getExampleOfAllKeys(getKey(getKey(prefix, partialPropertyName.getName()), "0"), partialPropertyName.getAttributes()));
                    result.addAll(getExampleOfAllKeys(getKey(getKey(prefix, partialPropertyName.getName()), "1"), partialPropertyName.getAttributes()));
                    return result;
                }
            } else {
                return getExampleOfAllKeys(getKey(prefix, partialPropertyName.getName()), partialPropertyName.getAttributes());
            }
        } else {
            return Collections.singletonList(getKey(prefix, partialPropertyName.getName()));
        }
    }


    private List<PartialPropertyName> mapAllIn(String packageName) throws IOException, ClassNotFoundException {
        List<PartialPropertyName> result = new ArrayList<>();
        List<Class<?>> classesFromPackageName = AnnotationUtils.getClassesFromPackageName(packageName);
        for (Class<?> aClass : classesFromPackageName) {
            // We don't want to process internal (static) classes, just the real attributes
            if (aClass.getName().contains("$")) {
                continue;
            }

            for (Field field : aClass.getFields()) {
                result.add(recursiveGetPartialPropertyName(field.getName(), field.getType()));
            }
        }
        return result;
    }

    private PartialPropertyName recursiveGetPartialPropertyName(String name, Class<?> type) {
        types.put(name, type);
        boolean isArray = type.isArray();
        if (isArray) {
            List<PartialPropertyName> partialPropertyNames = new ArrayList<>();
            if (!type.getComponentType().isAssignableFrom(String.class)) {
                for (Field fieldField : type.getComponentType().getFields()) {
                    partialPropertyNames.add(recursiveGetPartialPropertyName(fieldField.getName(), fieldField.getType()));
                }
            }
            return new PartialPropertyName(name, partialPropertyNames, isArray);
        } else {
            if (type.isAssignableFrom(String.class)) {
                return new PartialPropertyName(name, null, false);
            }
            List<PartialPropertyName> partialPropertyNames = new ArrayList<>();
            for (Field field : type.getFields()) {
                partialPropertyNames.add(recursiveGetPartialPropertyName(field.getName(), field.getType()));
            }
            return new PartialPropertyName(name, partialPropertyNames, false);
        }
    }

    private String getKey(String prefix, String end) {
        if (prefix == null) {
            return end;
        }
        return prefix + "." + end;
    }

    private <T> T mapAllToClass(String prefix, PartialPropertyName partialPropertyName, Class<T> mainClass) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchFieldException {
        String name = partialPropertyName.getName();
        if (partialPropertyName.getAttributes() == null) {
            String key = getKey(prefix, name);
            T instance = getInstance(System.getProperty(key), mainClass);
            return instance;
        } else if (partialPropertyName.getAttributes().isEmpty()) {
            String property;
            List<String> list = new ArrayList<>();
            int attemp2 = 0;
            do {
                String newPrefix = prefix == null ? partialPropertyName.getName() : prefix + "." + partialPropertyName.getName();
                property = System.getProperty(newPrefix + "." + (attemp2++));
                if (property != null) {
                    list.add(property);
                }
            } while (property != null);
            return getArrayInstance(list, mainClass);
        }

        List<Object> list = new ArrayList<>();
        Object subResult = null;
        List result = new ArrayList();
        int i = 0;
        do {
            for (PartialPropertyName attribute : partialPropertyName.getAttributes()) {
                String newPrefix = null;
                if (!partialPropertyName.isRoot()) {
                    if (partialPropertyName.isArray()) {
                        newPrefix = prefix == null ? partialPropertyName.getName() + "." + i : prefix + "." + i + "." + partialPropertyName.getName();
                    } else {
                        newPrefix = prefix == null ? partialPropertyName.getName() : prefix + "." + partialPropertyName.getName();
                    }
                }

                Class<?> arrayType = mainClass.getComponentType();
                Field field;
                if (arrayType == null) {
                    field = mainClass.getField(attribute.getName());
                } else {
                    field = arrayType.getField(attribute.getName());
                }
                subResult = mapAllToClass(newPrefix, attribute, field.getType());
                if (subResult != null) {
                    list.add(subResult);
                } else {
                    break;
                }
            }
            i++;
            if (subResult != null) {
                result.add(getInstance2(list, getTypeArrayOrNot(mainClass)));
                list.clear();
            }
        } while (!partialPropertyName.isRoot() && subResult != null && mainClass.getComponentType() != null);
        Object o = Array.newInstance(mainClass.getComponentType() == null ? mainClass : mainClass.getComponentType(), result.size());
        for (int i1 = 0; i1 < result.size(); i1++) {
            Array.set(o, i1, result.get(i1));
        }

        return (T) o;
    }

    private Class<?> getTypeArrayOrNot(Class<?> c) {
        return c.getComponentType() == null ? c : c.getComponentType();
    }

    private <T> T getInstance2(List<Object> list, Class<T> mainClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return (T) mainClass.getConstructors()[0].newInstance(list.toArray());
    }

    private <T> T getInstance(String property, Class<T> mainClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (property == null) {
            return null;
        }
        if (mainClass.isAssignableFrom(String.class)) {
            return (T) property;
        }
        return (T) mainClass.getConstructors()[0].newInstance(property);
    }

    private <T> T getArrayInstance(List<String> parameters, Class<T> mainClass) {
        Object o = Array.newInstance(mainClass.getComponentType(), parameters.size());
        for (int i = 0; i < parameters.size(); i++) {
            Array.set(o, i, parameters.get(i));
        }
        return (T) o;
    }
}
