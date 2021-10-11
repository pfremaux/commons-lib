package commons.lib.tooling.java.v1.code;

import java.util.ArrayList;
import java.util.List;

public class ReferenceClass {
    private final String packageLocation;
    private final String className;
    private final List<ClassAttribute> classAttributes = new ArrayList<>(); // TODO PFR clarifier avec class utilisatrice
    private final List<CodeMethod> codeMethods = new ArrayList<>();

    public ReferenceClass(String packageLocation, String className) {
        this.packageLocation = packageLocation;
        this.className = className;
    }

    public static ReferenceClass fromExistingClass(String pathOfClass) {
        int i = pathOfClass.lastIndexOf(".");
        final String pack;
        final String className;
        if (i > 0) {
            pack = pathOfClass.substring(0, i);
            className = pathOfClass.substring(i + 1);
        } else {
            pack = "";
            className = "";
        }
        // Class<?> aClass = ReferenceClass.class.getClassLoader().loadClass(pathOfClass);
        return new ReferenceClass(pack, className);
        //referenceClass.classAttributes.add(new ClassAttribute(true, false, new CodeObject()));
    }

    public CodeMethod getMethod(String name) {
        return getCodeMethods().stream().filter(m -> !m.isStatic()).filter(m->m.getName().equals(name)).findFirst().get();
    }

    public CodeMethod getStaticMethod(String name) {
        return getCodeMethods().stream().filter(CodeMethod::isStatic).filter(m->m.getName().equals(name)).findFirst().get();
    }

    public String getPackageLocation() {
        return packageLocation;
    }

    public String getClassName() {
        return className;
    }

    public List<CodeMethod> getCodeMethods() {
        return codeMethods;
    }

    public String getImportPath() {
        return this.getPackageLocation() + "." + this.getClassName();
    }
}
