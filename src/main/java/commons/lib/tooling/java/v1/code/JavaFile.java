package commons.lib.tooling.java.v1.code;

import commons.lib.main.StringUtils;
import commons.lib.tooling.java.v1.MethodGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class JavaFile {

    private final String name;
    private final ReferenceClass referenceClass;
    private final Set<String> imports = new HashSet<>();

    private final List<ClassAttribute> attributes = new ArrayList<>();
    private final List<CodeMethod> methods = new ArrayList<>();
    private final JavaFile extendedFile;

    public JavaFile(String packageLocation, String name, JavaFile extendedFile) {
        this.extendedFile = extendedFile;
        this.name = name;
        referenceClass = new ReferenceClass(packageLocation, name);
    }

    public static JavaFile pojoOf(String pack, String name, Map<String, String> attributes) {
        final JavaFile pojo = new JavaFile(pack, name, null);
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            final ReferenceClass attributeReferenceClass = ReferenceClass.fromExistingClass(entry.getValue());
            final String attributeName = entry.getKey();
            final CodeObject codeObject = new CodeObject(attributeReferenceClass, attributeName);
            pojo.withAttribute(false, true, codeObject);
            pojo.withMethod(Scope.PUBLIC, "get" + StringUtils.capitalize(attributeName, true), List.of())
                    .returnObject(codeObject)
            .endMethod();
        }

        return pojo;
    }


    public void build(StringBuilder builder) {
        prepareBuild();
        builder.append("package ");
        builder.append(referenceClass.getPackageLocation());
        builder.append(";\n");
        imports.forEach(pack -> {
            builder.append("import ");
            builder.append(pack);
            builder.append(";\n");

        });

        builder.append("public class ");
        builder.append(name);
        if (extendedFile != null) {
            builder.append("extends ");
            builder.append(extendedFile.name);
        }
        builder.append(" {\n");
        attributes.forEach(attribute -> attribute.build(builder, 1));
        methods.forEach(method -> method.build(builder, 1));
        builder.append("}");
    }

    public JavaFile withAttribute(boolean isStatic, boolean isFinal, ReferenceClass cl, String name) {
        ClassAttribute classAttribute = new ClassAttribute(isFinal, isStatic, new CodeObject(cl, name));
        attributes.add(classAttribute);
        return this;
    }

    public JavaFile withAttribute(boolean isStatic, boolean isFinal, CodeObject object) {
        ClassAttribute classAttribute = new ClassAttribute(isFinal, isStatic, object);
        attributes.add(classAttribute);
        return this;
    }

    public MethodGenerator withMethod(Scope scope, String name, List<CodeObject> parameters) {
        return new MethodGenerator(this, scope, name, parameters);
    }


    public String getName() {
        return name;
    }

    public ReferenceClass getReferenceClass() {
        return referenceClass;
    }

    public List<CodeMethod> getMethods() {
        return methods;
    }

    private void prepareBuild() {
        final Set<String> packagesToImport = new HashSet<>();
        for (CodeMethod method : methods) {
            packagesToImport.addAll(method.getTypesReferenced()
                    .stream()
                    .map(ReferenceClass::getImportPath)
                    .collect(Collectors.toSet()));
        }

        for (ClassAttribute attribute : attributes) {
            packagesToImport.add(attribute.getObject().getReferenceClass().getImportPath());
        }
        imports.addAll(packagesToImport);
        final List<ClassAttribute> attributesFinal = attributes.stream().filter(ClassAttribute::isFinal).collect(Collectors.toList());

        methods.add(0, CodeMethod.getConstructorInstance(Scope.PUBLIC, referenceClass, attributesFinal));
    }

}
