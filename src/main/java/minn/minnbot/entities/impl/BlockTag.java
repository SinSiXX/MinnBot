package minn.minnbot.entities.impl;

import minn.minnbot.entities.Tag;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;

public class BlockTag implements Tag {

    private String response;
    private String name;

    public BlockTag(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public User getOwner() {
        return null;
    }

    @Override
    public Guild getGuild() {
        return null;
    }

    @Override
    public String response() {
        return "";
    }

    @Override
    public void setResponse(String response) {
        this.response = "";
    }
}
