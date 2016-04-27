package minn.minnbot.gui;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

import java.awt.*;

public class GuildListener extends ListenerAdapter {

	private Guild g;
	private TextArea textArea;

	public GuildListener(Guild g, TextArea textArea) {
		this.g = g;
		this.textArea = textArea;
	}

	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getGuild() == g) {
			try {
				String output = "\n#" + event.getChannel().getName() + " > " + event.getAuthor().getUsername() + "#"
						+ event.getAuthor().getDiscriminator() + " > " + event.getMessage().getContent();
				textArea.append(output);
			} catch (NullPointerException ignored) {
			}
		}
	}

}
