package commons.lib.watcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.BiFunction;

public class AsyncWatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AsyncWatcher.class);

    private final WatchService watcher;
    private final List<Path> dirWatched;
    private final CustomEventQueue customEventQueue;
    private final BiFunction<Path, Path, Boolean> action;

    public AsyncWatcher(WatchService watcher, List<Path> dirWatched, CustomEventQueue customEventQueue, BiFunction<Path, Path, Boolean> action) throws IOException {
        this.watcher = watcher;
        this.dirWatched = dirWatched;
        this.customEventQueue = customEventQueue;
        this.action = action;
        for (Path path : dirWatched) {
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
        }
    }

    @Override
    public void run() {
        for (; ; ) {

            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println(event.context().getClass());
                WatchEvent.Kind<?> kind = event.kind();

                System.out.println(kind);
                // This key is registered only
                // for ENTRY_CREATE events,
                // but an OVERFLOW event can
                // occur regardless if events
                // are lost or discarded.
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                // The filename is the
                // context of the event.
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();

                // Verify that the new
                //  file is a text file.
                try {
                    // Resolve the filename against the directory.
                    // If the filename is "test" and the directory is "foo",
                    // the resolved name is "test/foo".
                    for (Path dir : dirWatched) {
                        Path child = dir.resolve(filename);
                        if (Files.probeContentType(child) != null && !Files.probeContentType(child).equals("text/plain")) {
                            System.err.format("New file '%s'" +
                                    " is not a plain text file.%n", filename);
                        }
                        customEventQueue.add(action);
                        customEventQueue.schedule(dir, child);
                        customEventQueue.proces();
                    }

                } catch (IOException x) {
                    logger.error("Error while processing a watch event.", x);
                    continue;
                }

                logger.info("doing something with {}", filename);
                //Details left to reader....
            }

            // Reset the key -- this step is critical if you want to
            // receive further watch events.  If the key is no longer valid,
            // the directory is inaccessible so exit the loop.
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    public static BiFunction<Path, Path, Boolean> defaultFileWatcher() {
        return (dir, filename) -> {
            logger.info("custom event queue starting... {}", filename);
            if (!Files.isDirectory(filename)) {
                logger.info("{} is not a directory ", filename);
                try {
                    Files.copy(dir.resolve(filename), Paths.get(filename.toFile().getAbsolutePath() + ".bak"));
                } catch (IOException e) {
                    logger.error("Error while copying the file", e);
                    return false;
                }
            } else {
                logger.info("{} is a directory. Doing nothing.", filename);
            }
            return false;
        };
    }
}
