package minn.minnbot.entities.command.owner;

import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EntityUtil;
import minn.minnbot.util.MiscUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.user.UserAvatarUpdateEvent;
import net.dv8tion.jda.exceptions.RateLimitedException;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.AvatarUtil;

import java.io.IOException;
import java.util.List;

public class TrackAvatarCommand extends CommandAdapter {

    private Tracker tracker;

    public TrackAvatarCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        User u;
        List<User> mentions = event.message.getMentionedUsers();
        if (event.allArguments.isEmpty()) {
            u = event.author;
        } else if (mentions.isEmpty()) {
            u = EntityUtil.getUserByNameDisc(event.allArguments, event.jda);
        } else {
            u = mentions.get(0);
        }
        if (u == null) {
            event.sendMessage("User is unknown!");
            return;
        }
        if (tracker == null)
            tracker = new Tracker(u);
        else
            tracker.setId(u);
        if (tracker == null)
            event.sendMessage("Stopped tracking user's avatar!");
        else
            event.sendMessage("Now tracking user's avatar!");
    }

    @Override
    public String getAlias() {
        return "track [mention]";
    }

    public String example() {
        return "track Minn#6688";
    }

    private class Tracker extends ListenerAdapter {

        private String id;
        private JDA api;

        Tracker(User user) {
            user.getJDA().addEventListener(this);
            this.api = user.getJDA();
            setId(user);
        }

        void setId(User user) {
            if (user.getId().equals(id)) {
                api.removeEventListener(this);
                tracker = null;
                return;
            }
            this.id = user.getId();
            updateAvatar();
        }

        void updateAvatar() {
            try {
                MiscUtil.getAvatar(api.getUserById(id), img -> {
                    if(img == null) {
                        logger.logInfo("Avatar was not received!");
                        return;
                    }
                    api.getAccountManager().setAvatar(AvatarUtil.getAvatar(img)).update();
                });
            } catch (IOException | UnirestException | AssertionError | RateLimitedException e) {
                logger.logThrowable(e);
            }
        }

        public void onUserAvatarUpdate(UserAvatarUpdateEvent event) {
            if (event.getUser().getId().equals(id)) updateAvatar();
        }

    }

    public boolean requiresOwner() {
        return true;
    }

}
