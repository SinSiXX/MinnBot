package minn.minnbot.manager;

import net.dv8tion.jda.entities.Guild;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class QueueRequestManager {

    private static Map<String, Consumer<Boolean>> requests = new HashMap<>();
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(25, 50, 10L, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), r -> {
        Thread t = new Thread(r, "QueueRequest");
        t.setDaemon(true);
        t.setPriority(5);
        return t;
    });

    public static boolean isRequesting(Guild g) {
        return requests.containsKey(g.getId());
    }

    public static void requestEnqueue(Guild g, Consumer<Boolean> request) {
        if(isRequesting(g))
            request.accept(false);
        else {
            requests.put(g.getId(), request);
            try {
                executor.submit(() -> request.accept(true));
                executor.submit(() -> {
                    try {
                        Thread.sleep(TimeUnit.MINUTES.toMillis(5L));
                    } catch (InterruptedException ignored) {
                    }
                    dequeue(g);
                });
            } catch (RejectedExecutionException e) {
                request.accept(false);
            }
        }
    }

    public static void dequeue(Guild g) {
        if(isRequesting(g))
            requests.remove(g.getId());
    }

}
