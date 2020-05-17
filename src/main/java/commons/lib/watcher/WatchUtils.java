package commons.lib.watcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.Collections;

public class WatchUtils {

    public static Thread watch(Path path) throws IOException {
        return watch(path, new CustomEventQueue());
    }

    public static Thread watch(Path path, CustomEventQueue eventQueue) throws IOException {
        final WatchService watchService = FileSystems.getDefault().newWatchService();
        final AsyncWatcher asyncWatcher = new AsyncWatcher(
                watchService,
                Collections.singletonList(path),
                eventQueue,
                AsyncWatcher.defaultFileWatcher());
        return new Thread(asyncWatcher);
    }
}
