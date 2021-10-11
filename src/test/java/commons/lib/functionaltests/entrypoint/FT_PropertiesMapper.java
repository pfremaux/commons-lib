package commons.lib.functionaltests.entrypoint;

import commons.lib.functionaltests.settings.FunctionalTestsSettings;
import commons.lib.main.SystemUtils;
import commons.lib.main.filestructure.mapping.PropertiesMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FT_PropertiesMapper {

    public static void main(String[] args) {
        PropertiesMapper propertiesMapper = new PropertiesMapper();
        try {
            propertiesMapper.initMapper(getPackage());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            SystemUtils.failProgrammer();
        }
        final StringBuilder builder = propertiesMapper.getPropertiesExample();
        final String exampleBuilt = builder.toString();
        try {
            final String reference = Files.readString(Path.of(getExpectedExample()));
            final String[] exampleBuiltLines = exampleBuilt.split("\n");
            final String[] referenceLines = reference.split("\r\n");
            for (int i = 0; i < exampleBuiltLines.length; i++) {
                if (!referenceLines[i].equals(exampleBuiltLines[i])) {
                    System.out.printf("Row %d \nExpected : %s\nFound: %s", i, referenceLines[i], exampleBuiltLines[i]);
                    SystemUtils.failProgrammer();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            SystemUtils.failProgrammer();
        }

    }

    private static String getPackage() {
        return System.getProperty(FunctionalTestsSettings.PROPERTIES_MAPPER_ROOT_JAVA_PACKAGE, "commons.lib.properties.mapper");
    }

    private static String getExpectedExample() {
        return System.getProperty(FunctionalTestsSettings.MAIN_INPUT_DIR_PROP, "./commons-lib/src/test") + System.getProperty(FunctionalTestsSettings.PROPERTIES_MAPPER_EXPECTED_OUTPUT_PROPERTIES, "/resources/propertiesMapper/expected_example.properties");
    }

}
