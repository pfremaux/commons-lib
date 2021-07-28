package commons.lib.functionaltests.socket.secured;

import commons.lib.extra.server.socket.*;
import commons.lib.extra.server.socket.secured.SecuredSocketInitializer;
import commons.lib.functionaltests.settings.FunctionalTestsSettings;
import commons.lib.functionaltests.socket.ChatMessage;
import commons.lib.functionaltests.socket.ChatMessageConsumer;
import commons.lib.main.SystemUtils;
import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.CustomConsole;
import commons.lib.main.os.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecuredChat {
    private static final Logger logger = LoggerFactory.getLogger(SecuredChat.class);
    public static final String CONSOLE_INPUT_PROPERTY = "console.input";
    public static final int CHAT_MESSAGE_CODE = 5111;
    public static final Map<String, String> CLIENT_1_TO_CLIENT_2 = Map.of(
            // msg received -> response
            "hey", "hey",
            "how are you", "fine and you",
            "same for me", "ok bye",
            "bye bye", ""
    );
    public static final Map<String, String> CLIENT_2_TO_CLIENT_1 = Map.of(
            // msg received -> response
            "hey", "how are you",
            "fine and you", "same for me",
            "ok bye", "bye bye"
    );


    public static void main(String[] args) {
        SecuredChat securedChat = new SecuredChat();
        securedChat.run(args);
    }

    public static void generateInputFiles() {
        final List<String> fileContent1 = new ArrayList<>();
        final List<String> fileContent2 = new ArrayList<>();
        for (Map.Entry<String, String> entry : CLIENT_1_TO_CLIENT_2.entrySet()) {
            fileContent1.add(entry.getKey());
            fileContent2.add(entry.getValue());
        }
    }

    public void run(String[] args) {
        final String inputConsoleFilePath;
        if (args.length == 0) {
            inputConsoleFilePath = System.getProperty(CONSOLE_INPUT_PROPERTY);
            if (inputConsoleFilePath == null) {
                System.out.println("Input console not set. You must provide the path of the text file that contains all your input test. It's in the property : " + CONSOLE_INPUT_PROPERTY);
                SystemUtils.failUser();
            }
        } else {
            inputConsoleFilePath = args[0];
        }
        final CustomConsole console = ConsoleFactory.getInstance(Path.of(inputConsoleFilePath));
        console.printf("Starting with %s", inputConsoleFilePath);
        final MessageConsumerManager messageConsumerManager = new MessageConsumerManager();
        messageConsumerManager.register(CHAT_MESSAGE_CODE, new ChatMessageConsumer());
        SecuredSocketInitializer.registerEventsBinding(messageConsumerManager);
        final Map<Integer, Function<List<byte[]>, Wrapper>> wrappers = new HashMap<>();
        wrappers.put(5111, strings -> {
            LogUtils.debug("strings = {}", strings);
            return new Wrapper(
                    Message.bytesToInt(strings.get(0)),
                    new ChatMessage(
                            Message.bytesToString(strings.get(4)),
                            Message.bytesToString(strings.get(1)),
                            Message.bytesToInt(strings.get(2)),
                            Message.bytesToBool(strings.get(3))));
        });
        final WrapperFactory wrapperFactory = new WrapperFactory(wrappers);
        SecuredSocketInitializer.init(wrapperFactory);
        final String myHostname;
        final int myPort;
        final String distantHostname;
        final int distantPort;
        boolean begin;
        if (inputConsoleFilePath.endsWith("1.txt")) {
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myHostname = "localhost";
            distantHostname = "127.0.0.1";
            myPort = 11111;
            distantPort = 22222;
            begin = true;
        } else {
            myHostname = "127.0.0.1";
            distantHostname = "localhost";
            myPort = 22222;
            distantPort = 11111;
            begin = false;
        }
        final Server server = new Server(myHostname, myPort, 5, messageConsumerManager, wrapperFactory);

        try {

            if (begin) {
                LogUtils.debug("I'm client 1. Waiting before attempting a connection");
                Thread.sleep(4000L);
                LogUtils.debug("Waited enough.");
                final Client client = new Client(distantHostname, distantPort);
                LogUtils.debug("Asking for a secured connection.");
                client.secured(myHostname, myPort);
                final String msg = console.readLine();
                final ChatMessage chatMessage = new ChatMessage(msg, myHostname, myPort, true);
                final List<byte[]> strings = Stream.concat(Stream.of(Message.intToBytes(CHAT_MESSAGE_CODE)), Stream.of(chatMessage.serializeBytes())).collect(Collectors.toList());
                client.connectSendClose(
                        wrapperFactory
                                .getFunctionMap()
                                .get(CHAT_MESSAGE_CODE)
                                .apply(strings)
                                .serialize());
            }

            server.listen();
            final List<String> history = console.history();
            if (myPort == 11111) {
                Files.write(Path.of(FunctionalTestsSettings.SECURED_CHAT_OUTPUT_FILE_CLIENT_1_PROP), history);
            } else {
                Files.write(Path.of(FunctionalTestsSettings.SECURED_CHAT_OUTPUT_FILE_CLIENT_2_PROP), history);
            }
        } catch (InterruptedException | IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            SystemUtils.failSystem();
        }
        console.printf("Process ended");
    }

}
