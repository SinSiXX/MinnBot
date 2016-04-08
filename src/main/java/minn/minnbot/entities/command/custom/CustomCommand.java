package minn.minnbot.entities.command.custom;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.Command;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class CustomCommand extends ListenerAdapter implements Command {

	private User owner;
	private Guild guild;
	private String response;
	private String command;
	private String usage;
	
	
	public CustomCommand(User owner, Guild guild, String response, String command, String usage) {
		this.owner = owner;
		this.guild = guild;
		this.response = response;
		this.command = command;
		this.usage = usage;
	}

	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.isPrivate())
			return;
	}
	
	@Override
	public void setLogger(Logger logger) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCommand(CommandEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCommand(String message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String usage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlias() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiresOwner() {
		// TODO Auto-generated method stub
		return false;
	}

}
