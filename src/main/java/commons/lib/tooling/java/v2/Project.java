package commons.lib.tooling.java.v2;

import commons.lib.main.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static commons.lib.tooling.java.v2.CodeHelper.*;

public class Project {
    protected TreeMap<String, ClassDefinition> knownClasses;

    public Project(TreeMap<String, ClassDefinition> knownClasses) {
        this.knownClasses = knownClasses;
        CodeHelper.setProject(this);
        registerBase(this.getKnownClasses(), "java.lang", "Integer");
        registerBase(this.getKnownClasses(), "java.lang", "String");
        registerBase(this.getKnownClasses(), "java.lang", "Double");
        registerBase(this.getKnownClasses(), "java.lang", "Long");
        registerBase(this.getKnownClasses(), "java.sql", "SQLException");
        registerBase(this.getKnownClasses(), "java.sql", "Connection")
            .declarePublicMethod("prepareStatement", List.of( new ObjectDeclaration(null, c("String"), "req") ));
        registerBase(this.getKnownClasses(), "java.sql", "PreparedStatement")
                .declarePublicMethod("setInt", List.of(new ObjectDeclaration(null, c("Integer"), "i"), new ObjectDeclaration(null, c("Integer"), "value")));
        registerBase(this.getKnownClasses(), "java.sql", "DriverManager")
            .declarePublicStaticMethod(c("Connection"), "getConnection", List.of(new ObjectDeclaration(null, c("String"), "uri")));
    }

    public TreeMap<String, ClassDefinition> getKnownClasses() {
        return knownClasses;
    }

    public static void main(String[] args) {
        Project project = new Project(new TreeMap<>());

        CodeHelper.setProject(project);
        ClassDefinition classDefinition = new ClassDefinition(null, "mypack", "Coucou");
        classDefinition
                .declarePrivateFinalAttribute(c("S"), "name")
                .declarePublicStaticAttribute(c("L"), "id")
                .end()
                .declarePublicMethod( "generateId", List.of())
                .declare(c("L"), "myId")
                .assign("myId", v("5"))
                .return_("myId")
                .end();
        classDefinition.declarePublicMethod("getName", List.of())
                .return_("name")
                .end();
        project.getKnownClasses().put("p", classDefinition);
        ClassDefinition classDefinition1 = new ClassDefinition(null, "mypack2", "Salut");
        project.getKnownClasses().put("p2", classDefinition1);
        classDefinition1
                .declarePrivateFinalAttribute(c("S"), "message")
                .end();
        classDefinition1
                .declarePublicMethod("display", List.of(new ObjectDeclaration(null, c("p"), "user", (String) null)))
                .call("user", "getName")
                .storeLastResultIn(c("St"), "fds")
                .bulkLine("System.out.println(message +\" \" + fds);\n")
                    //.try_()
                    //.bulkLine("//try something")
                    .catch_(c("SQLExc"), "e")
                    .bulkLine("//TODO catch\n")
                    .declare(c("I"), "toot")
                    .end()
        ;

        ClassDefinition p = project.getKnownClasses().get("p");
        StringBuilder b = new StringBuilder();
        p.build(b, 0);
        System.out.println(b.toString());

        p = project.getKnownClasses().get("p2");
        b = new StringBuilder();
        p.build(b, 0);
        System.out.println(b.toString());
        ClassDefinition pojo = project.createPojo("mypack.pojo", "User", Map.of("name", "String", "id", "Long"));
        b = new StringBuilder();
        pojo.build(b, 0);
        System.out.println(b.toString());
    }

    public ClassDefinition createPojo(String pack, String name, Map<String, String> attributes) {
        ClassDefinition pojo = new ClassDefinition(null, pack, name);
        pojo.getClassDirectlyReferenced().putAll(this.getKnownClasses());
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            pojo.declarePrivateFinalAttribute(entry.getValue(), entry.getKey())
            .end();
            pojo.declarePublicMethod( "get"+StringUtils.capitalize(true, entry.getKey()), List.of())
                .return_(entry.getKey())
            .end();
        }
        return pojo;
    }

    public static ClassDefinition registerBase(TreeMap<String, ClassDefinition> map, String pack, String name) {
        final ClassDefinition value = new ClassDefinition(null, pack, name);
        map.put(name, value);
        return value;
    }

    public ClassDefinition newClass(String project_package_output_dao, String daoClassName) {
        ClassDefinition definition = new ClassDefinition(null, project_package_output_dao, daoClassName);
        definition.getClassDirectlyReferenced().putAll(this.getKnownClasses());
        return definition;
    }
}
