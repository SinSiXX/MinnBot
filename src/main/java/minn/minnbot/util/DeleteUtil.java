package minn.minnbot.util;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.impl.JDAImpl;
import net.dv8tion.jda.exceptions.RateLimitedException;
import net.dv8tion.jda.requests.Requester;
import org.json.JSONArray;
import org.json.JSONObject;

import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DeleteUtil {

    // BULK

    public synchronized static void deleteIn(List<Message> messageList, TextChannel channel, Consumer<List<Exception>> callback) {
        if(messageList == null || messageList.isEmpty() || channel == null)
            return;

        Thread t = new Thread(() -> {
            JDA api = channel.getJDA();
            Requester requester = ((JDAImpl) api).getRequester();

            List<List<String>> histories = getSplitIDs(messageList);

            List<Exception> exceptions = delete(histories, requester, channel.getId());
            if (callback != null)
                callback.accept(exceptions);
        });
        t.setDaemon(true);
        t.start();
    }

    public synchronized static void deleteFrom(TextChannel channel, Consumer<List<Exception>> callback, User user, int... amount) {
        if (user == null || channel == null)
            return;

        Thread t = new Thread(() -> {
            JDA api = channel.getJDA();
            Requester requester = ((JDAImpl) api).getRequester();
            int count = 99;
            if (amount.length > 0)
                count = amount[0];

            List<Message> hist = new MessageHistory(channel).retrieve(count).parallelStream().filter(m -> m.getAuthor() == user).collect(Collectors.toList());
            List<List<String>> histories = getSplitIDs(hist);

            List<Exception> exceptions = delete(histories, requester, channel.getId());
            if (callback != null)
                callback.accept(exceptions);
        });
        t.setDaemon(true);
        t.start();
    }

    public synchronized static void deleteFrom(TextChannel channel, Consumer<List<Exception>> callback, int... amount) {
        if (channel == null)
            return;

        Thread t = new Thread(() -> {
            JDA api = channel.getJDA();
            Requester requester = ((JDAImpl) api).getRequester();
            int count = 99;
            if (amount.length > 0)
                count = amount[0];

            List<Message> hist = new MessageHistory(channel).retrieve(count);
            List<List<String>> histories = getSplitIDs(hist);

            List<Exception> exceptions = delete(histories, requester, channel.getId());
            if (callback != null)
                callback.accept(exceptions);
        });
        t.setDaemon(true);
        t.start();
    }

    private static synchronized List<Exception> delete(List<List<String>> histories, Requester requester, String id) {
        List<Exception> exceptions = new LinkedList<>();
        for (List<String> list : histories) {
            if (list.size() <= 0) {
                exceptions.add(new IllegalArgumentException("MessageList/Array was empty!"));
                continue;
            }

            Requester.Response response = null;
            if (list.size() == 1)
                response = requester.delete(Requester.DISCORD_API_PREFIX + "channels/" + id + "/messages/" + list.get(0));
            else
                response = requester.post(Requester.DISCORD_API_PREFIX + "channels/" + id + "/messages/bulk_delete", new JSONObject().put("messages", new JSONArray(list.toString())));

            // Check if it worked or not
            if (response.isRateLimit()) {
                exceptions.add(new RateLimitedException(response.getObject().getLong("retry_after")));
            } else if (!response.isOk()) {
                exceptions.add(new UnexpectedException("" + response.code));
            }

            // Avoid rate limitation.
            try {
                Thread.sleep(1100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return exceptions;
    }

    private static List<List<String>> getSplitIDs(List<Message> messages) {
        if (messages == null)
            return new LinkedList<>();
        List<String> listing = new LinkedList<>();

        messages.parallelStream().forEachOrdered(m -> listing.add(m.getId()));
        List<List<String>> histories = new LinkedList<>();

        List<String> current = listing;
        histories.add(current);


        while (current.size() > 99) {
            List<String> next = new LinkedList<>();
            next.addAll(current.subList(99, current.size()));
            current.subList(99, current.size()).clear();
            histories.add(next);
            current = next;
        }
        return histories;
    }

}
