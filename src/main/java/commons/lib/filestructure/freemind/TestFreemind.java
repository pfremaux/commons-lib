package commons.lib.filestructure.freemind;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Random;

import static commons.lib.filestructure.freemind.SimplifiedNode.node;


public class TestFreemind {

    public static final Random RANDOM = new Random(System.currentTimeMillis());

    // run with -Dconsole.encoding=UTF-8 -Dfile.encoding=UTF-8
    public static void main(String[] args) throws TransformerException, ParserConfigurationException {
        SimplifiedNode root = node("root")
                .with(
                        node("child 1"),
                        node("child 2"),
                        node("child 3")
                                .with(
                                        node("child 1 of child 3"),
                                        node("child 2 of child 3")
                                )

                );
        FreemindRoot freemindRoot = FreemindRoot.getDefaultInstance();
        freemindRoot.getChildren().add(root.getFreemindInstance());
        // root.getFreemindInstance()
        Freemind.saveFreemindFile("./file.mm", freemindRoot);
    }

}
