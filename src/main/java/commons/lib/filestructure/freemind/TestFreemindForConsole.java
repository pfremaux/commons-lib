package commons.lib.filestructure.freemind;


import commons.lib.console.v2.action.PostProcessorType;
import commons.lib.console.v2.yaml.YamlAction;
import commons.lib.console.v2.yaml.YamlPostProcessor;
import commons.lib.console.v2.yaml.YamlQuestion;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.Yaml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TestFreemindForConsole {

    public static List<YamlAction> actions = new ArrayList<>();

    // run with -Dconsole.encoding=UTF-8 -Dfile.encoding=UTF-8
    public static void main(String[] args) throws TransformerException, ParserConfigurationException, IOException, SAXException {
        FreemindRoot freemindRoot = Freemind.loadFreemindFile("c:/dev/app.mm");
        for (FreemindNode child : freemindRoot.getChildren()) {
            if (child.getText().equals("app")) {
                // root node !
                for (FreemindNode freemindNode : child.getFreemindNodes()) {
                    YamlAction yamlAction = getYamlAction(freemindNode);
                    actions.add(yamlAction);
                }
            }
        }

        Yaml yaml = new Yaml();
        FileWriter writer = new FileWriter("./youpi.yaml");
        yaml.dump(actions, writer);
        //FreemindRoot freemindRoot = FreemindRoot.getDefaultInstance();
        //freemindRoot.getChildren().add(root.getFreemindInstance());
        // root.getFreemindInstance()
        //Freemind.saveFreemindFile("./file.mm", freemindRoot);
    }

    private static YamlAction getYamlAction(FreemindNode node) {
        String id = node.getId();
        String title = node.getText();
        List<YamlQuestion> questions = new ArrayList<>();
        String info = null;
        PostProcessorType postProcessType = null;
        List<String> subChoice = new ArrayList<>();

        for (FreemindNode question : node.getFreemindNodes()) {
            String text = question.getText();
            questions.add(new YamlQuestion(text));
            for (FreemindNode annexe : question.getFreemindNodes()) {
                String value = annexe.getText();
                if (value.startsWith("s:")) {
                    info = value.substring(2);
                    postProcessType = PostProcessorType.SAVE_CACHE;
                } else if (value.startsWith("c:")) {// TODO fixme
                    // implementationClassName
                    info = value.substring(2);
                    postProcessType = PostProcessorType.CUSTOM;
                } else {
                    YamlAction yamlAction = getYamlAction(annexe);
                    actions.add(yamlAction);
                    subChoice.add(yamlAction.getChoiceName());
                }
            }
        }
        YamlPostProcessor postProcessor = new YamlPostProcessor(info, postProcessType);
        // title is used as the name of the action for simplicity. but it should have a specific name
        // here title will be the text displayed in the console before the question are triggered
        return new YamlAction(id, title, questions, subChoice, postProcessor);
    }

}
