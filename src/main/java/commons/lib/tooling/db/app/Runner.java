package commons.lib.tooling.db.app;

import commons.lib.main.AnnotationsConsumer;
import commons.lib.main.StringUtils;
import commons.lib.main.console.v4.AbstractCliApp;
import commons.lib.tooling.db.annotation.Table;
import commons.lib.tooling.db.dbdescription.DbTableDescription;
import commons.lib.tooling.db.java2sql.Converter;
import commons.lib.tooling.java.v1.ProjectGenerator;
import commons.lib.tooling.java.v1.code.CodeObject;
import commons.lib.tooling.java.v1.code.ReferenceClass;
import commons.lib.tooling.java.v1.code.Scope;
import commons.lib.tooling.java.v2.*;

import java.nio.file.Path;
import java.util.*;

import static commons.lib.tooling.java.v2.CodeHelper.*;

public class Runner extends AbstractCliApp {

    public static void main(String[] args) {
        final Runner runner = new Runner();
        runner.register(Arrays.asList(
                new ParameterDomainClassPackage(),
                new ParameterProjectRoot(),
                new ParameterResourcesRelativePath()
        ));
        final Map<String, String> trustedParameters = runner.validateInput(new String[]{"-p", "/home/shinichi/IdeaProjects/commons-lib", "-r", "./src/resources", "-d", "commons.lib.tooling.db.java2sql.test.v2"});
        for (Map.Entry<String, String> entry : trustedParameters.entrySet()) {
            System.out.println(entry.getValue());
        }
        final Path PROJECT_ROOT_PATH = Path.of(trustedParameters.get(ParameterProjectRoot.PROPERTY_KEY));
        final Path PROJECT_RESOURCE_PATH = PROJECT_ROOT_PATH.resolve(trustedParameters.get(ParameterResourcesRelativePath.PROPERTY_KEY));
        final String PROJECT_PACKAGE_ANALYSIS = trustedParameters.get(ParameterDomainClassPackage.PROPERTY_KEY);
        final Path PROJECT_JAVA_ANALYSIS_PATH = PROJECT_ROOT_PATH.resolve("/src/main/java").resolve(PROJECT_PACKAGE_ANALYSIS.replaceAll("\\.", "/"));

        String PROJECT_PACKAGE_OUTPUT_DAO = "";
        go(PROJECT_ROOT_PATH, PROJECT_RESOURCE_PATH, PROJECT_PACKAGE_ANALYSIS, PROJECT_JAVA_ANALYSIS_PATH, PROJECT_PACKAGE_OUTPUT_DAO);
    }

    private static void goLegacy(Path project_root_path, Path project_resource_path, String project_package_path, Path project_java_analysis_path, String PROJECT_PACKAGE_OUTPUT_DAO) {
        final Map<String, DbTableDescription> dbTableDescriptionsV1 = new HashMap<>();
        ProjectGenerator projectGenerator = new ProjectGenerator();
        ProjectGenerator.referenceAllClasses(Set.of("java.sql.Connection", "java.lang.String", "java.lang.Integer", "java.sql.DriverManager"));
        AnnotationsConsumer annotationsConsumer = new AnnotationsConsumer(
                project_package_path,
                Map.of(Table.class, Converter.createTableDescriptionConsumer(dbTableDescriptionsV1)));
        for (Map.Entry<String, DbTableDescription> entry : dbTableDescriptionsV1.entrySet()) {
            final DbTableDescription table = entry.getValue();
            String daoClassName = StringUtils.capitalize(true, table.getTableName(), "dao");
            projectGenerator.newClassFile(PROJECT_PACKAGE_OUTPUT_DAO, daoClassName)
                    // lines.add("\tprivate static final String URI = \"" + jdbcUri + "\";");
                    .withAttribute(false, true, ReferenceClass.fromExistingClass("java.lang.String"), "uri")
                    .withMethod(Scope.PUBLIC, "connect", List.of())
                    //.call()//return DriverManager.getConnection(URI);
                    .endMethod()
                    .withMethod(Scope.PUBLIC, "select", List.of(new CodeObject(ReferenceClass.fromExistingClass("java.lang.Integer"), "id")))
                    .endMethod()
            //.returnObject(new CodeObject())
            ;
            System.out.println(entry.getValue());
        }
    }

    private static void go(Path project_root_path, Path project_resource_path, String project_package_path, Path project_java_analysis_path, String PROJECT_PACKAGE_OUTPUT_DAO) {
        final Map<String, DbTableDescription> dbTableDescriptionsV2 = new HashMap<>();
        Project projectGenerator = new Project(new TreeMap<>());

        AnnotationsConsumer annotationsConsumer = new AnnotationsConsumer(
                project_package_path,
                Map.of(Table.class, Converter.createTableDescriptionConsumer(dbTableDescriptionsV2)));
        for (Map.Entry<String, DbTableDescription> entry : dbTableDescriptionsV2.entrySet()) {
            final DbTableDescription table = entry.getValue();
            String daoClassName = StringUtils.capitalize(true, table.getTableName(), "dao");
            ClassDefinition classDefinition = projectGenerator.newClass(PROJECT_PACKAGE_OUTPUT_DAO, daoClassName);
            // lines.add("\tprivate static final String URI = \"" + jdbcUri + "\";");
            ClassBlock classBlock = classDefinition.declarePrivateFinalAttribute("String", "uri");
            Block connectMethod = classBlock
                    .declarePublicMethod("connect", List.of());
            connectMethod
                    .try_()
                        .call(c("DriverManager"), "getConnection", new ObjectUsage(null, "uri", false, null))
                        .storeLastResultIn(c("Connection"), "c")
                        .return_("c")
                        //.bulkLine("return DriverManager.getConnection(URI);\n")
                        .end();
            connectMethod.catch_(projectGenerator.getKnownClasses().ceilingEntry("SQL").getValue(), "e")
                    .bulkLine("throw new RuntimeException(e);\n")
            .end();



            classBlock.declarePublicMethod( "select", List.of(new ObjectDeclaration(null, c("Integer"), "id")))
                    .declare(c("String"), "request", v("SELECT * FROM ..."))
                    .call((String) null, "connect")
                    .storeLastResultIn(c("Connection"), "connection")
                    //PreparedStatement ps = conn.prepareStatement("selec
                    .call("connection", "prepareStatement", a(classDefinition, "uri").use())
                    .storeLastResultIn(c("PreparedStatement"), "ps")
                    .storeLastResultInTry()
                    .call("ps", "setInt", new ObjectUsage(null, null, "1", null), o("id"))
            ;
                    //.storeLastResultInTry()

            StringBuilder b = new StringBuilder();
            classDefinition.build(b, 0);
            System.out.println(b.toString());
            // System.out.println(entry.getValue());
        }
    }

}
