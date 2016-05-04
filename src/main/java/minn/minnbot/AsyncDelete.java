package minn.minnbot;

import net.dv8tion.jda.entities.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class AsyncDelete {

    private static final ExecutorService executor = new ThreadPoolExecutor(1,1,1L, TimeUnit.MINUTES,new LinkedBlockingDeque<>(), r -> {
        final Thread thread = new Thread(r, "AsyncDeletion");
        thread.setDaemon(true);
        thread.setPriority(1);
        return thread;
    });

    public static void deleteAsync(Message message, Consumer<?> callback) {
        executor.execute(() -> {
            message.deleteMessage();
            callback.accept(null);
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(1L) / 5);
            } catch (InterruptedException ignored) {
            }
        });
    }

}
