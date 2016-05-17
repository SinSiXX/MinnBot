package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

import java.time.format.DateTimeFormatter;

public class SayCommand extends CommandAdapter {

	public SayCommand(String prefix, Logger logger) {
		init(prefix, logger);
	}

	@Override
	public void onCommand(CommandEvent event) {
		if(event.allArguments.isEmpty())
			return;

		String args = "\u0001" + event.allArguments
				.replaceAll("([Ww][Hh][Oo0]\\s*[Aa][Mm]\\s*[Ii1]\\s*\\??)", String.format(" **You are %s!** ", event.author.getUsername()))
				.replaceAll("([Ww][hH][Aa][Tt7]\\s*[Tt7][iI1][mM][Ee3]\\s*[I1i][sS5]\\s*[Ii1][Tt7]\\s*\\??)", String.format( " **It is %s!** ", event.message.getTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a"))))
				.replaceAll("([Hh][Oo0][Ww]\\s*[Mm][Aa][Nn][Yy]\\s*(([Uu][Ss5][Ee3][Rr][Ss5])|([Mm][eE3][mM][Bb][Ee3][Rr][Ss5]))\\s*[Aa][Rr][Ee3]\\s*(([Hh][Ee3][Rr][Ee])|([iI1][nN]\\s*[tT7][Hh][Ii1][Ss5]\\s*(([Gg9][uU][iI1][Ll][Dd])|([Ss5][Ee3][Rr][Vv][Ee3][Rr]))))\\s*\\??)", String.format(" **There are %d users in this guild!%s** ", event.guild.getUsers().size(), event.isPrivate ? "" : String.format("\nAnd %d of those have access to this channel!", event.event.getTextChannel().getUsers().size())).replace("100", ":100:"))
				.replaceAll("([Ii1]\\s*[Aa][Mm]\\s+)|([Ii1]'[Mm]\\s+)", " **you are** ")
				.replaceAll("([Aa][mM]\\s*[1iI])", " **are you** ")
				.replaceAll("([Gg9][Aa][Yy])", " **straight** ")
				.replaceAll("([Ff][Aa][Gg9])", " **swag** ")
				.replaceAll("([Cc][Ll][Oo0][Uu][Dd])", " **butt** ")
				.replaceAll("(\\s+[Mm][Yy]\\s+)", " **your** ")
				.replaceAll("(\\s+[Mm][Ee3]\\s+)", " **you** ")
				.replaceAll("([Cc][Oo0][Oo0][Ll])", " **kewl** ")
				.replaceAll("([Dd][Oo0][Ee3][Ss5]\\s*[Tt][Hh][Ii1][Ss5]\\s*[Ww][Oo0][Rr][Kk]\\s*\\??)", "**No!**");

		event.sendMessage(args);
	}

	@Override
	public String getAlias() {
		return "say <arguments>";
	}

	@Override
	public String example() {
		return "say who am i?";
	}

}
