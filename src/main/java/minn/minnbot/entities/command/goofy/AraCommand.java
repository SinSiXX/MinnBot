package minn.minnbot.entities.command.goofy;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import org.json.JSONArray;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AraCommand extends CommandAdapter {

    private AraListener listener;

    public AraCommand(String prefix, Logger logger, JDA api) {
        init(prefix, logger);
        Thread t = new Thread(() -> {
            listener = new AraListener(api);
        });
        t.start();
    }

    @Override
    public void onCommand(CommandEvent event) {
        event.sendMessage(listener.getQuote());
    }

    @Override
    public String getAlias() {
        return "ara";
    }

    private class AraListener extends ListenerAdapter {

        private String id = "94203228043874304"; // ara id
        private List<String> quotes;
        private Thread autoSaver;

        AraListener(JDA api) {
            api.addEventListener(this);
            load();
            if (autoSaver != null) {
                autoSaver.interrupt();
            }
            autoSaver = new Thread(() -> {
                while (!autoSaver.isInterrupted()) {
                    try {
                        Thread.sleep(TimeUnit.HOURS.toMillis(1L));
                    } catch (InterruptedException ignored) {
                        break;
                    }
                    save();
                }
            });
            autoSaver.setDaemon(true);
            autoSaver.setName("Ara-AutoSave");
            autoSaver.start();
        }

        String getQuote() {
            if (quotes.isEmpty())
                return "No quotes found.";
            return quotes.get(new Random().nextInt(quotes.size()));
        }

        public void onMessageReceived(MessageReceivedEvent event) {
            if (event.getAuthor().getId().equals(id) && event.getMessage().getMentionedUsers().isEmpty() && event.getMessage().getAttachments().isEmpty()) { // memes
                quotes.add(event.getMessage().getContent());
            }
        }

        private JSONArray getJSONArray() {
            JSONArray arr = new JSONArray();
            quotes.parallelStream().forEachOrdered(arr::put);
            return arr;
        }

        private void save() {
            Writer out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ara.json"), "UTF-8"));
                try {
                    out.write(getJSONArray().toString(4));
                } finally {
                    //noinspection ThrowFromFinallyBlock
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void load() {
            quotes = new LinkedList<>();
            if (!new File("ara.json").exists())
                return;
            try {
                JSONArray arr = new JSONArray(new String(Files.readAllBytes(Paths.get("ara.json"))));
                arr.forEach(obj -> quotes.add((String) obj));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
