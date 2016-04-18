package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.audio.MinnPlayer;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PlayCommand extends CommandAdapter {

    private MinnPlayer player;
    private File folder = new File("Playlist");

    public PlayCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
        if (folder.exists())
            folder.delete();
        folder.mkdirs();
        folder.deleteOnExit();
        player = new MinnPlayer();
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    public MinnPlayer getPlayer() {
        return player;
    }

    @Override
    public void onCommand(CommandEvent event) {
        List<Message.Attachment> atts = event.event.getMessage().getAttachments();
        if (atts.size() < 1) {
            event.sendMessage(usage());
            return;
        }
        File f = new File(folder.getPath() + "\\" + atts.get(0).getFileName());
        if (atts.get(0).download(f)) {
            try {
                player.add(f);
                f.deleteOnExit();
                event.sendMessage("Added file to queue.");
            } catch (UnsupportedAudioFileException e) {
                event.sendMessage("File extension is not allowed. `" + e.getMessage() + "`");
            } catch (IOException e) {
                event.sendMessage("File is not a file. `" + e.getMessage() + "`");
            }
        } else {
            event.sendMessage("I was unable to download attached file.");
        }
    }

    @Override
    public boolean isCommand(String message) {
        String[] parts = message.split(" ", 2);
        if (parts.length < 1)
            return false;
        return parts[0].equalsIgnoreCase(prefix + "play");
    }

    public String usage() {
        return "Upload an audio file and type " + prefix + "play in the comment box.";
    }

    @Override
    public String getAlias() {
        return "play";
    }
}
