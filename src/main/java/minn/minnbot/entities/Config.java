package minn.minnbot.entities;

import minn.minnbot.MinnBot;
import net.dv8tion.jda.JDA;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

public interface Config { // TODO: Implement

    String mashapeKey();

    String giphyKey();

    String ownerId();

    String googleApi();

    /**
     * This is used for direct access to the discord api.
     * @return JDA a Java Discord API instance.
     */
    JDA jda();

    /**
     * Used to get managers etc.
     * @return The current instance of MinnBot
     */
    MinnBot bot();

    Config read(File file) throws JSONException, IOException;


}
