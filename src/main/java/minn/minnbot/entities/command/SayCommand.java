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
				.replaceAll("((?i)wh[o0]\\s*[4a]m\\s*[i1]\\s*\\??)", String.format(" **You are %s!** ", event.author.getUsername()))
				.replaceAll("((?i)wh[4a][t7]\\s*[t7][i1]m[e3]\\s*[1i][s5]\\s*[i1][t7]\\s*\\??)", String.format( " **It is %s!** ", event.message.getTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a"))))
				.replaceAll("((?i)h[o0]w\\s*m[4a]ny\\s*(([Uu][Ss5][Ee3][Rr][Ss5])|([Mm][eE3][mM][Bb][Ee3][Rr][Ss5]))\\s*[Aa][Rr][Ee3]\\s*(([Hh][Ee3][Rr][Ee])|((([iI1][nN])|([Oo0][Nn]))\\s*[tT7][Hh][Ii1][Ss5]\\s*(([Gg9][uU][iI1][Ll][Dd])|([Ss5][Ee3][Rr][Vv][Ee3][Rr]))))\\s*\\??)", String.format(" **There are %d users in this guild!%s** ", event.guild.getUsers().size(), event.isPrivate ? "" : String.format("\nAnd %d of those have access to this channel!", event.event.getTextChannel().getUsers().size())).replace("100", ":100:"))
				.replaceAll("((?i)[i1]\\s*[4a]m\\s+)|([i1]'m)", " **you are** ")
				.replaceAll("((?i)[4a]m\\s*[1i])", " **are you** ")
				.replaceAll("((?i)[g9][4a]y)", " **straight** ")
				.replaceAll("((?i)f[4a][g9])", " **swag** ")
				.replaceAll("((?i)cl[o0]ud)", " **butt** ")
				.replaceAll("((?i)\\s+my\\s+)", " **your** ")
				.replaceAll("((?i)\\s+m[e3]\\s+)", " **you** ")
				.replaceAll("((?i)c[o0][o0]l)", " **kewl** ")
				.replaceAll("((?i)d[o0][e3][s5]\\s*th[i1][s5]\\s*w[o0]rk\\s*\\??)", "**No!**");
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
