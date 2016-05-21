package minn.minnbot.entities.command.owner;

import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EmoteUtil;
import minn.minnbot.util.EntityUtil;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.exceptions.RateLimitedException;
import net.dv8tion.jda.utils.AvatarUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class AvatarCommand extends CommandAdapter {

    public AvatarCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        Thread t = new Thread(() -> {
            List<User> mentions = event.message.getMentionedUsers();
            final Message[] msg = {null};
            final boolean[] sent = {false};
            Consumer<BufferedImage> callback = b -> {
                if(b == null) {
                    if(sent[0])
                        msg[0].updateMessageAsync("I was unable to get that avatar!", null);
                    return;
                }
                event.jda.getAccountManager().setAvatar(AvatarUtil.getAvatar(b)).update();
                if (sent[0]) msg[0].updateMessageAsync("Successfully updated avatar! " + EmoteUtil.getRngThumbsup(), null);
            };
            event.sendMessage("Grabbing avatar...", m -> {
                sent[0] = m != null;
                if(sent[0])
                    msg[0] = m;
                try {
                    if (event.allArguments.isEmpty()) {
                        minn.minnbot.util.MiscUtil.getAvatar(event.author, callback);
                    } else if (mentions.isEmpty()) {
                        User u = EntityUtil.getUserByNameDisc(event.allArguments, event.jda);
                        if (u == null) {
                            if (sent[0]) m.updateMessageAsync("User is not known!", null);
                            return;
                        }
                        minn.minnbot.util.MiscUtil.getAvatar(u, callback);
                    } else {
                        minn.minnbot.util.MiscUtil.getAvatar(mentions.get(0), callback);
                    }
                } catch (IOException | AssertionError | RateLimitedException | UnirestException e) {
                    if (sent[0])
                        m.updateMessageAsync(String.format("I was unable to get that avatar! **%s**", e.getMessage()), null);
                }
            });
        });
        t.setName("UpdatingAvatar");
        t.setDaemon(true);
        t.setPriority(1);
        t.start();
    }


    @Override
    public String getAlias() {
        return "stealAvatar [mention]";
    }

    public String example() {
        return "stealAvatar Minn#6688";
    }

    public boolean requiresOwner() {
        return true;
    }

}
