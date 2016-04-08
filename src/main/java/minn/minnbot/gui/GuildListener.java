package minn.minnbot.gui;

import java.awt.TextArea;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

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
			} catch (NullPointerException e) {
			}
		}
	}

}
