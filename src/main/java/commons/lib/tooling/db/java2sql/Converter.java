package commons.lib.tooling.db.java2sql;

import commons.lib.main.StringUtils;
import commons.lib.main.SystemUtils;
import commons.lib.tooling.db.dbdescription.ColumnDescription;
import commons.lib.tooling.db.dbdescription.DbTableDescription;
import commons.lib.tooling.db.annotation.NToN;
import commons.lib.tooling.db.annotation.NToOne;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

public class Converter {




    public static Consumer<Class<?>> createTableDescriptionConsumer(Map<String, DbTableDescription> dbTableDescriptions) {
        return (Class<?> c) -> {
            final String tableName = StringUtils.snakeCase(c.getSimpleName().toLowerCase());
            final DbTableDescription dbTableDescription = dbTableDescriptions.computeIfAbsent(tableName, k -> new DbTableDescription());
            dbTableDescription.setTableName(tableName);
            dbTableDescription.addColumn(new ColumnDescription(tableName + "_id", Long.class, true, "-1"));
            for (Field declaredField : c.getDeclaredFields()) {
                final NToN nToNAnnotation = declaredField.getAnnotation(NToN.class);
                final NToOne nToOneAnnotation = declaredField.getAnnotation(NToOne.class);
                if (nToNAnnotation != null) {
                    if (Collection.class.isAssignableFrom(declaredField.getType())
                            || Map.class.isAssignableFrom(declaredField.getType())) {
                        if (declaredField.getType().getTypeParameters().length > 0) {
                            final ParameterizedType parameterizedType = (ParameterizedType) declaredField.getGenericType();
                            final Class<?> actualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                            final String subTableName = StringUtils.snakeCase(actualTypeArgument.getSimpleName());
                            final String intermediaryTableName = tableName + "_" + subTableName;
                            final DbTableDescription intermediaryTableDescription = dbTableDescriptions.computeIfAbsent(intermediaryTableName, k -> new DbTableDescription());
                            intermediaryTableDescription.setTableName(intermediaryTableName);
                            intermediaryTableDescription.addColumn(new ColumnDescription(intermediaryTableName + "_id", Long.class, true, "-1"));
                            intermediaryTableDescription.addFK(tableName);
                            intermediaryTableDescription.addFK(subTableName);
                            dbTableDescription.addFK(intermediaryTableName);
                        }
                    } else {
                        System.err.println("Error : for field " + declaredField.getName() + " in class " + c.getSimpleName() + ". Annotation " + NToN.class + " is meant to be used for maps of collection.");
                        SystemUtils.failUser();
                    }
                } else if (nToOneAnnotation != null) {
                    if (Collection.class.isAssignableFrom(declaredField.getType())
                            || Map.class.isAssignableFrom(declaredField.getType())) {
                        if (declaredField.getType().getTypeParameters().length > 0) {
                            final ParameterizedType parameterizedType = (ParameterizedType) declaredField.getGenericType();
                            final Class<?> actualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                            final String subTable = StringUtils.snakeCase(actualTypeArgument.getSimpleName());
                            final DbTableDescription dbTableAttrDescription = dbTableDescriptions.computeIfAbsent(subTable, k -> new DbTableDescription());
                            dbTableAttrDescription.setTableName(subTable);
                            if (!dbTableAttrDescription.getColumns().containsKey("id_" + tableName)) {
                                ColumnDescription columnDescription = new ColumnDescription("id_" + tableName, Long.class, true, "-1");
                                dbTableAttrDescription.addColumn(columnDescription);
                            }
                        }
                    } else {
                        System.err.println("Error : for field " + declaredField.getName() + " in class " + c.getSimpleName() + ". Annotation " + NToOne.class + " is meant to be used for maps of collection.");
                        SystemUtils.failUser();
                    }
                } else {
                    if (Collection.class.isAssignableFrom(declaredField.getType())
                            || Map.class.isAssignableFrom(declaredField.getType())) {
                        System.err.println("Error : for field " + declaredField.getName() + " in class " + c.getSimpleName() + ". This field must be annotated with either" + NToOne.class.getSimpleName() + " or " + NToN.class.getSimpleName() + " as it can contain multiple values.");
                        SystemUtils.failUser();
                    }
                    dbTableDescription.addColumn(new ColumnDescription(declaredField.getName(), declaredField.getType(), true, "-1"));
                }
            }
        };
    }
}
