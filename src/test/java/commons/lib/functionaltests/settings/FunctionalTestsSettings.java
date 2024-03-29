package commons.lib.functionaltests.settings;

import commons.lib.functionaltests.entrypoint.FT_ConsoleV3;

public class FunctionalTestsSettings {
    public static final String MAIN_INPUT_DIR_PROP = "functional.tests.input.dir";
    public static final String MAIN_OUTPUT_DIR_PROP = "functional.tests.output.dir";
    public static final String UNSECURED_CHAT_INPUT_FILE_CLIENT_1_PROP = "unsecured.chat.input.file.client.1";
    public static final String UNSECURED_CHAT_INPUT_FILE_CLIENT_2_PROP = "unsecured.chat.input.file.client.2";
    public static final String UNSECURED_CHAT_OUTPUT_FILE_CLIENT_1_PROP = "unsecured.chat.output.file.client.1";
    public static final String UNSECURED_CHAT_OUTPUT_FILE_CLIENT_2_PROP = "unsecured.chat.output.file.client.2";
    public static final String UNSECURED_CHAT_EXPECTED_OUTPUT_FILE_CLIENT_1_PROP = "unsecured.chat.expected.output.file.client.1";
    public static final String UNSECURED_CHAT_EXPECTED_OUTPUT_FILE_CLIENT_2_PROP = "unsecured.chat.expected.output.file.client.2";

    public static final String SECURED_CHAT_INPUT_FILE_CLIENT_1_PROP = "secured.chat.input.file.client.1";
    public static final String SECURED_CHAT_INPUT_FILE_CLIENT_2_PROP = "secured.chat.input.file.client.2";
    public static final String SECURED_CHAT_OUTPUT_FILE_CLIENT_1_PROP = "secured.chat.output.file.client.1";
    public static final String SECURED_CHAT_OUTPUT_FILE_CLIENT_2_PROP = "secured.chat.output.file.client.2";
    public static final String SECURED_CHAT_EXPECTED_OUTPUT_FILE_CLIENT_1_PROP = "secured.chat.expected.output.file.client.1";
    public static final String SECURED_CHAT_EXPECTED_OUTPUT_FILE_CLIENT_2_PROP = "secured.chat.expected.output.file.client.2";

    public static final String CONSOLE_V3_INPUT_FILE = "console.v3.input.console";


    public static final String PROPERTIES_MAPPER_ROOT_JAVA_PACKAGE = "properties.mapper.root.package.name";
    public static final String PROPERTIES_MAPPER_EXPECTED_OUTPUT_PROPERTIES = "properties.mapper.expected.output.file";

    public static final String[][] DEFAULT_PROPERTIES = {
            {MAIN_INPUT_DIR_PROP, ".\\commons-lib\\src\\test\\resources"},
            {MAIN_OUTPUT_DIR_PROP, ".\\"},

            // UNSECURED CHAT
            {UNSECURED_CHAT_INPUT_FILE_CLIENT_1_PROP, "socket/client1.txt"},
            {UNSECURED_CHAT_INPUT_FILE_CLIENT_2_PROP, "socket/client2.txt"},
            {UNSECURED_CHAT_OUTPUT_FILE_CLIENT_1_PROP, "output_client1.txt"},
            {UNSECURED_CHAT_OUTPUT_FILE_CLIENT_2_PROP, "output_client2.txt"},
            {UNSECURED_CHAT_EXPECTED_OUTPUT_FILE_CLIENT_1_PROP, "expected_output_client1.txt"},
            {UNSECURED_CHAT_EXPECTED_OUTPUT_FILE_CLIENT_2_PROP, "expected_output_client2.txt"},

            // SECURED CHAT
            {SECURED_CHAT_INPUT_FILE_CLIENT_1_PROP, "socket/client1.txt"},
            {SECURED_CHAT_INPUT_FILE_CLIENT_2_PROP, "socket/client2.txt"},
            {SECURED_CHAT_OUTPUT_FILE_CLIENT_1_PROP, "output_client1.txt"},
            {SECURED_CHAT_OUTPUT_FILE_CLIENT_2_PROP, "output_client2.txt"},
            {SECURED_CHAT_EXPECTED_OUTPUT_FILE_CLIENT_1_PROP, "expected_output_client1.txt"},
            {SECURED_CHAT_EXPECTED_OUTPUT_FILE_CLIENT_2_PROP, "expected_output_client2.txt"},

            // CONSOLE V3
            {CONSOLE_V3_INPUT_FILE, "/console/v3/input_console.txt"},

            // PROPERTIES MAPPER
            {PROPERTIES_MAPPER_ROOT_JAVA_PACKAGE, "commons.lib.functionaltests.properties.mapper"},
            {PROPERTIES_MAPPER_EXPECTED_OUTPUT_PROPERTIES, "/propertiesMapper/expected_example.properties"},
    };

    public static final Class<?>[] REGISTERED_TESTS = new Class<?>[]{
            // FT_UnsecuredChat.class,
            //FT_SecuredChat.class,
             FT_ConsoleV3.class,
            //FT_PropertiesMapper.class
    };

    private FunctionalTestsSettings() {

    }


}
