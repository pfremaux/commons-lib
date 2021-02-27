package commons.lib.tooling.markdown;

import commons.lib.main.AnnotationUtils;
import commons.lib.main.FileUtils;
import commons.lib.tooling.documentation.Documentation;
import commons.lib.tooling.documentation.MdDoc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateMd {

    private final String basePackageName;

    public GenerateMd(String basePackageName) {
        this.basePackageName = basePackageName;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        final GenerateMd generateMd = new GenerateMd("commons.lib");
        generateMd.run();
    }

    public void run() throws IOException, ClassNotFoundException {
        final Map<String, Documentation> docs = new HashMap<>();
        final List<Class<?>> classesFromPackageName = AnnotationUtils.getClassesFromPackageName(basePackageName);
        for (Class<?> aClass : classesFromPackageName) {
            Documentation documentation;
            final String packageName = aClass.getPackageName();
            final String title = packageName.substring(basePackageName.length());
            final MdDoc[] annotations = aClass.getAnnotationsByType(MdDoc.class);
            if (annotations.length > 0) {
                documentation = docs.computeIfAbsent(packageName, packName -> new Documentation());
                documentation.title(1, title);
                documentation.setFileName(aClass.getSimpleName() + ".md");
                documentation.title(2, aClass.getSimpleName());
                documentation.text(annotations[0].description(), true);
                // System.out.println(aClass + " : " + annotations[0].description());
                for (Method declaredMethod : aClass.getDeclaredMethods()) {
                    final MdDoc annotation = declaredMethod.getDeclaredAnnotation(MdDoc.class);
                    if (annotation != null) {
                        final String strParameters = Stream.of(declaredMethod.getParameters())
                                .map(parameter -> parameter.getType().getSimpleName())
                                .collect(Collectors.joining());
                        documentation.title(3, declaredMethod.getName() + "(" + strParameters + ")");
                        documentation.text(annotation.description(), true);
                        for (String example : annotation.examples()) {
                            documentation.exampleCode(example);
                        }
                        //System.out.println("Method : " + declaredMethod + " : " + annotation.description());
                        Parameter[] parameters = declaredMethod.getParameters();
                        for (Parameter parameter : parameters) {
                            MdDoc declaredAnnotation = parameter.getDeclaredAnnotation(MdDoc.class);
                            if (declaredAnnotation != null) {
                                documentation.pinPoint(parameter.getType().getSimpleName() + " : "
                                        + declaredAnnotation.description(), true);
                            } else {
                                documentation.pinPoint(parameter.getType().getSimpleName() + "(no description)", true);
                            }
                        }
                    }
                }
            }
        }
        final MarkdownGenerator markdownGenerator = new MarkdownGenerator(Paths.get("."));
        for (Map.Entry<String, Documentation> entry : docs.entrySet()) {
            final String packages = entry.getKey().replace(".", "/");
            Documentation documentation = entry.getValue();
            FileUtils.recursiveCreateDirectory(
                    markdownGenerator.getBaseDocumentationDir().toAbsolutePath(),
                    markdownGenerator.getBaseDocumentationDir().resolve(packages).toAbsolutePath().toString()
            );
            markdownGenerator.generate(documentation, packages + "/" + documentation.getFileName());
        }
    }
}
