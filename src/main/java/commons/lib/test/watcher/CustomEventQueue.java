package commons.lib.test.watcher;

import commons.lib.main.UnrecoverableException;

import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiFunction;

public class CustomEventQueue {
    private final Queue<BiFunction<Path, Path, Boolean>> queue = new LinkedBlockingQueue<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private Runnable watcher;

    public void schedule(Path dir, Path file) {
        watcher = () -> {
            BiFunction<Path, Path, Boolean> function;
            while ((function = queue.poll()) != null) {
                Boolean retry = function.apply(dir, file);
                if (retry != null && retry) {
                    queue.add(function);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new UnrecoverableException(e.getMessage(), e.getMessage(), e, -1);
                    }
                }
            }
        };
    }

    public void proces() {
        executorService.submit(watcher);
    }

    public void add(BiFunction<Path, Path, Boolean> function) {
        queue.add(function);
    }

}
