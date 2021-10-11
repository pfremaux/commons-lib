package commons.lib.main;

import commons.lib.main.os.LogUtils;
import commons.lib.tooling.db.dbscript.DbScriptCreator;
import commons.lib.tooling.db.java2sql.Converter;
import commons.lib.tooling.db.dbdescription.DbTableDescription;
import commons.lib.tooling.db.annotation.Table;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class AnnotationsConsumer {

// TODO MOVE/REMOVE
    public static void main(String[] args) {
        final Map<String, DbTableDescription> dbTableDescriptionsV1 = new HashMap<>();
        final Map<String, DbTableDescription> dbTableDescriptionsV2 = new HashMap<>();
        final AnnotationsConsumer annotationsConsumerV1 = new AnnotationsConsumer(
                "commons.lib.tooling.db.java2sql.test.v1",
                Map.of(Table.class, Converter.createTableDescriptionConsumer(dbTableDescriptionsV1)));
        for (Map.Entry<String, DbTableDescription> entry : dbTableDescriptionsV1.entrySet()) {
            System.out.println(entry.getValue().toPrettyString());
        }

        final AnnotationsConsumer annotationsConsumerV2 = new AnnotationsConsumer(
                "commons.lib.tooling.db.java2sql.test.v2",
                Map.of(Table.class, Converter.createTableDescriptionConsumer(dbTableDescriptionsV2)));

        StringBuilder builder = DbScriptCreator.generateSql(new ArrayList<>(dbTableDescriptionsV1.values()));
        System.out.println(builder.toString());
    }

    public AnnotationsConsumer(String packageName, Map<Class<? extends Annotation>, Consumer<Class<?>>> generators) {
        try {
            LogUtils.debug("Searching annotated classes in package " + packageName);
            final List<Class<?>> classes;
            classes = AnnotationUtils.getClassesFromPackageName(packageName);
            LogUtils.debug("Found " + classes.size() + " class to analyze");
            for (Class aClass : classes) {
                process(aClass, generators);
            }
        } catch (ClassNotFoundException | IOException e) {
            final String errorMsg = "Error while processing the annotated classes.";
            LogUtils.error(errorMsg + " " + e.getMessage());
            throw new UnrecoverableException(
                    errorMsg,
                    new String[]{"An error due to a programmer's mistake forced the application to stop"},
                    e,
                    SystemUtils.EXIT_PROGRAMMER_ERROR);
        }
    }


    private void process(Class<? extends Annotation> aClass, Map<Class<? extends Annotation>, Consumer<Class<?>>> generators) {
        LogUtils.debug("is " + aClass + " an annotation of encryption ?");
        for (Class<? extends Annotation> annotationClass : generators.keySet()) {
            if (aClass.isAnnotationPresent(annotationClass)) {
                LogUtils.debug(aClass + " is an annotation of encryption");
                if (generators.containsKey(annotationClass)) {
                    generators.get(annotationClass).accept(aClass);
                }
                final Field[] declaredFields = aClass.getDeclaredFields();
                for (Class<? extends Annotation> aClass1 : generators.keySet()) {
                    for (Field declaredField : declaredFields) {
                        Annotation annotation = declaredField.getAnnotation(aClass1);
                        if (annotation != null && generators.containsKey(aClass1)) {
                            generators.get(aClass1).accept(declaredField.getClass());
                        }
                    }
                }
            }
        }
    }


}
