package commons.lib.main.console.v2.yaml;

import commons.lib.main.console.v2.action.PostProcessorType;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class YamlLoader {

    public static void main(String[] args) throws IOException {
        YamlLoader loader = new YamlLoader();
        List<YamlAction> yamlActions = loader.readYaml("E:\\dev\\intellij\\passwords\\file2.yaml");
        for (YamlAction yamlAction : yamlActions) {
            System.out.println(yamlAction.getChoiceName());
            for (YamlQuestion yamlQuestion : yamlAction.getQuestionList()) {
                System.out.println("\t" + yamlQuestion.getQuestion());
            }
            YamlPostProcessor postProcessorType = yamlAction.getPostProcessorType();
            if (postProcessorType != null) {
                System.out.println(postProcessorType.getYamlPostProcessor()
                        + " : "
                        + postProcessorType.getInfo());
            }
        }
    }

    public List<YamlAction> readYaml(String path) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        ArrayList<Object> objects = (ArrayList<Object>) yaml.load(new FileReader(new File(path)));
        List<YamlAction> result = new ArrayList<>();
        for (Object obj : objects) {
            result.add((YamlAction) obj);
        }
        return result;
    }

    public void testDumpReader() throws IOException {
        Yaml yaml = new Yaml();
        Object objects = yaml.load(new FileReader(new File("E:\\dev\\intellij\\passwords\\file.yaml")));
        for (Object obj : (ArrayList<Object>) objects) {
            System.out.println(((YamlAction) obj).getChoiceName());
        }

    }

    public void testDumpWriter() throws IOException {
        List<YamlAction> result = new ArrayList<>();
        result.add(
                new YamlAction(
                        "askPren",
                        "Set first name",
                        Arrays.asList(new YamlQuestion("Give your first name.")),
                        Arrays.asList("askPPhra", "showPhra"),
                        new YamlPostProcessor("prenom", PostProcessorType.SAVE_CACHE)
                ));

        result.add(
                new YamlAction(
                        "askPPhra",
                        "Set first name",
                        Collections.singletonList(new YamlQuestion("kel est la phrase pattern ?")),
                        Collections.emptyList(),
                        new YamlPostProcessor("phrase", PostProcessorType.SAVE_CACHE)
                ));

        result.add(
                new YamlAction(
                        "showPhra",
                        "Set first name",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        new YamlPostProcessor("firstName", PostProcessorType.SAVE_CACHE)
                ));

        Yaml yaml = new Yaml();
        FileWriter writer = new FileWriter("./file.yaml");
        yaml.dump(result, writer);
    }
}
