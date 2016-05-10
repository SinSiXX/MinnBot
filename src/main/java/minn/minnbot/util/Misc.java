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
import java.util.stream.Collectors;

public class Misc {

    public synchronized static void deleteFrom(TextChannel channel, User user, int... amount) {
        if (user == null || channel == null)
            return;
        JDA api = channel.getJDA();
        Requester requester = ((JDAImpl) api).getRequester();
        int count = 100;
        if (amount.length > 0)
            count = amount[0];

        List<Message> hist = new MessageHistory(channel).retrieve(count).parallelStream().filter(m -> m.getAuthor() == user).collect(Collectors.toList());
        List<String> messageList = new LinkedList<>();
        hist.stream().forEach(m -> messageList.add(m.getId()));
        List<List<String>> histories = new LinkedList<>();

        if ((messageList.size() % 99) > 0 && (messageList.size() % 99) != messageList.size()) {
            List<String> current = new LinkedList<>();
            current.addAll(messageList);
            histories.add(current);
            while (histories.get(histories.size() - 1).size() > 99) {
                List<String> next = new LinkedList<>();
                List<String> sub = current.subList(100, current.size());
                next.addAll(sub);
                current.removeAll(sub);
                histories.add(next);
            }
            delete(histories, requester, channel.getId());
            return;
        }
        List<List<String>> list = new LinkedList<>();
        list.add(messageList);
        delete(list, requester, channel.getId());
    }

    public synchronized static void deleteFrom(TextChannel channel, int... amount) {
        JDA api = channel.getJDA();
        Requester requester = ((JDAImpl) api).getRequester();
        int count = 99;
        if (amount.length > 0)
            count = amount[0];

        List<Message> hist = new MessageHistory(channel).retrieve(count);
        List<String> messageList = new LinkedList<>();
        hist.stream().forEach(m -> messageList.add(m.getId()));
        List<List<String>> histories = new LinkedList<>();
        List<String> current = new LinkedList<>();

        current.addAll(messageList);

        if (current.size() > 99) {
            histories.add(current);
            while (histories.get(histories.size() - 1).size() > 99) {
                List<String> next = new LinkedList<>();
                List<String> sub = current.subList(99, current.size());
                next.addAll(sub);
                current.removeAll(sub);
                histories.add(next);
            }
            delete(histories, requester, channel.getId());
            return;
        }
        List<List<String>> list = new LinkedList<>();
        list.add(messageList);
        delete(list, requester, channel.getId());

    }

    private static synchronized void delete(List<List<String>> histories, Requester requester, String id) {
        histories.parallelStream().forEach(list -> {
            Requester.Response response = null;
            if (list.size() <= 0) {
                new IllegalArgumentException("MessageList/Array was empty!").printStackTrace();
                return;
            } else if (list.size() == 1)
                response = requester.delete(Requester.DISCORD_API_PREFIX + "channels/" + id + "/messages/" + list.get(0));
            else
                response = requester.post(Requester.DISCORD_API_PREFIX + "channels/" + id + "/messages/bulk_delete", new JSONObject().put("messages", new JSONArray(list.toString())));
            // System.out.println("Object: \n" + response.getObject() + "\nCode: " + response.code);

            // Check if it worked or not
            if (response.isRateLimit()) {
                new RateLimitedException(response.getObject().getLong("retry_after")).printStackTrace();
            } else if (!response.isOk()) {
                new UnexpectedException("" + response.code).printStackTrace();
            }

            // Avoid rate limitation.
            try {
                Thread.sleep(1100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


}
