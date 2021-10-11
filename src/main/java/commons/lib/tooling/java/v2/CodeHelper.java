package commons.lib.tooling.java.v2;


import commons.lib.main.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CodeHelper {

    private static Project project;

    private CodeHelper() {

    }

    public static void setProject(Project project) {
        CodeHelper.project = project;
    }


    public static ClassDefinition c(String aliasClass) {
        Map.Entry<String, ClassDefinition> stringClassDefinitionEntry = project.getKnownClasses().ceilingEntry(aliasClass);
        if (stringClassDefinitionEntry == null) {
            return null;
        }
        return stringClassDefinitionEntry.getValue();
    }

    public static List<ObjectDeclaration> p(ObjectDeclaration... ps) {
        return List.of(ps);
    }

    public static AttributeDeclaration a(ClassDefinition c,  String attributeName) {
        return (AttributeDeclaration) c.getBlockLines().get(attributeName);
    }

    public static ObjectDeclaration d(CodeElement parent, String type, String name) {
        final Map.Entry<String, ClassDefinition> entry = project.getKnownClasses().ceilingEntry(type);
        final ClassDefinition value = entry.getValue();
        return new ObjectDeclaration(parent == null ? value.getParent() : parent, value, name, (String) null);
    }

    public static ObjectUsage o(String aliasObject) {
        return new ObjectUsage((method) -> method.getObjectDirectlyReferenced().get(aliasObject).use());
    }

    public static Call call(ObjectUsage caller, String methodDeclared, ObjectUsage... parameters) {
        return new Call(caller, methodDeclared, Arrays.asList(parameters));
    }

    public static ObjectUsage v(String v) {
        if (!StringUtils.isNumber(v)) {
            return new ObjectUsage(null, null, "\"" + v + "\"", null);
        } else {
            return new ObjectUsage(null, null, v, null);
        }
    }


}
