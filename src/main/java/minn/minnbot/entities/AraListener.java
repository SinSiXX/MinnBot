package minn.minnbot.entities;

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

public class AraListener extends ListenerAdapter {

    private JDA api;
    private String id = "94203228043874304"; // ara id
    private List<String> quotes;
    private Thread autoSaver;

    public AraListener(JDA api) {
        this.api = api;
        load();
        if(autoSaver != null) {
            autoSaver.interrupt();
        }
        autoSaver = new Thread(() -> {
           while(!autoSaver.isInterrupted()) {
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

    public String getQuote() {
        if(quotes.isEmpty())
            return "No quotes found.";
        return quotes.get(new Random().nextInt(quotes.size()));
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().getId().equals(id) && event.getMessage().getMentionedUsers().isEmpty()) {
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
        if(!new File("ara.json").exists())
            return;
        try {
            JSONArray arr = new JSONArray(new String(Files.readAllBytes(Paths.get("ara.json"))));
            arr.forEach(obj -> quotes.add((String) obj));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
