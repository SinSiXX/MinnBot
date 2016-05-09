package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.User;

public class InfoCommand extends CommandAdapter {

    private String ownername;
    private String inviteUrl;
    private final boolean bot;

    public InfoCommand(String prefix, Logger logger, User owner, String inviteUrl, boolean bot) {
        this.prefix = prefix;
        this.logger = logger;
        if (owner != null)
            this.ownername = owner.getUsername();
        else
            this.ownername = "unknown";
        if (inviteUrl != null)
            this.inviteUrl = inviteUrl;
        else
            this.inviteUrl = "";
        this.bot = bot; // Possible client support
    }

    @Override
    public void onCommand(CommandEvent event) {
        StringBuilder builder = new StringBuilder("**__Info page:__**\n\n"); // Use builder for better performance
        builder.append("I'm the second incarnation of MinnBot:tm:\n");
        builder.append("Coded in JDA(").append(net.dv8tion.jda.JDAInfo.VERSION).append(") by my creator Minn, I was selected to work on behalf of **").append(ownername).append("**.\n");
        builder.append("You can view my commands by typing **").append(prefix).append("help** in the chat.\n");
        builder.append("If you want to see my **source code**,").append(" here is a link to my github page: **<http://minndevelopment.github.io/MinnBot>**\n");
        builder.append("Visit the official development server for further information: **<https://discord.gg/0mcttggeFpcMByUz>**\n");
        if (!inviteUrl.isEmpty() && bot)
            builder.append("Make me join your server with this url:\n").append("**<").append(inviteUrl).append(">**");
        event.sendMessage(builder.toString());
    }

    @Override
    public String getAlias() {
        return "info";
    }

}
