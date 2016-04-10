package minn.minnbot.entities.impl;

import minn.minnbot.entities.Tag;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;

public class TagImpl implements Tag {

    private User owner;
    private Guild guild;
    private String response;
    private String name;

    public TagImpl(User owner, Guild guild, String name, String response) {
        if(owner == null || guild == null || name.isEmpty() || response.isEmpty()) {
            throw new IllegalArgumentException("Something went wrong. Use the help command.");
        }
        this.owner = owner;
        this.guild = guild;
        this.response = response;
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public Guild getGuild() {
        return guild;
    }

    @Override
    public String response() {
        return response;
    }

    @Override
    public void setResponse(String response) {
        this.response = response;
    }
}
