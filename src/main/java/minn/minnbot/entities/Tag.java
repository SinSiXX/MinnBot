package minn.minnbot.entities;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;

public interface Tag {

    User getOwner();

    Guild getGuild();

    String response();

    String name();

    void setResponse(String response);

}
