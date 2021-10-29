package commons.lib.tooling.java.v1;

import commons.lib.tooling.java.v1.code.CodeObject;
import commons.lib.tooling.java.v1.code.JavaFile;
import commons.lib.tooling.java.v1.code.ReferenceClass;
import commons.lib.tooling.java.v1.code.Scope;
import commons.lib.tooling.java.v1.code.line.Return;

import java.util.List;
import java.util.Map;

public class TestJavaGen {

    public static void main(String[] args) {
        final ProjectGenerator projectGenerator = new ProjectGenerator();
        final JavaFile javaFile1 = projectGenerator.newClassFile("toto.titi", "MaClass");
        final ReferenceClass integer = ProjectGenerator.ALL_CLASSES.get("Integer");
        final ReferenceClass maClass = ProjectGenerator.ALL_CLASSES.get("MaClass");
        final CodeObject param1 = new CodeObject(integer, "param1");
        javaFile1.withMethod(Scope.PROTECTED, "maMethod", List.of(param1))
                .returnObject(new Return(new CodeObject(integer, param1.getName())))
                .endMethod();
        StringBuilder builder = new StringBuilder();
        javaFile1.build(builder);
        System.out.println(builder.toString());

        final JavaFile javaFile2 = projectGenerator.newClassFile("boum", "Class2");
        final CodeObject result = new CodeObject(integer, "result");
        final CodeObject parameter1 = new CodeObject(maClass, "hello");
        javaFile2.withAttribute(false, true, maClass, "testAmoi");
        javaFile2.withMethod(Scope.PUBLIC, "coucou", List.of(parameter1))
                .declare(result, true)
                .call(obj -> obj.getReferenceClass().getCodeMethods().stream().filter(m -> m.getName().equals("maMethod")).findFirst().get(),
                        parameter1,
                        List.of(new CodeObject(integer, "5")),
                        result)
                .returnObject(result)
                .endMethod();

        builder = new StringBuilder();
        javaFile2.build(builder);
        System.out.println(builder.toString());
        JavaFile javaFile = projectGenerator.newPojo("pojos", "User", Map.of("name", String.class.getName(), "birth", Long.class.getName()));
        builder = new StringBuilder();
        javaFile.build(builder);
        System.out.println(builder.toString());
    }

}
