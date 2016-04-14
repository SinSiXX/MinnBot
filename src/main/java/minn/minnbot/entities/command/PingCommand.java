package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class PingCommand extends CommandAdapter {

    private InetAddress address;

    public PingCommand(String prefix, Logger logger) throws UnknownHostException {
        this.prefix = prefix;
        this.logger = logger;
        try {
            this.address = InetAddress.getByName("discordapp.com");
        } catch (UnknownHostException e) {
            logger.logError(e);
            throw e;
        }
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (isCommand(event.getMessage().getContent())) {
            logger.logCommandUse(event.getMessage());
            onCommand(new CommandEvent(event));
        }
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            long ping = System.currentTimeMillis();
            Socket s = new Socket();
            s.connect(new InetSocketAddress(address, 443), 200);
            ping = System.currentTimeMillis() - ping;
            event.sendMessage("**__Ping:__** " + ((ping > 200) ? ">200ms :anger:" : "***" + ping + "ms***"));
        } catch (Exception e) {
            logger.logError(e);
        }
    }

    @Override
    public boolean isCommand(String message) {
        try {
            message = message.toLowerCase();
            if (!message.startsWith(prefix))
                return false;
            message = message.substring(prefix.length());
            String command = message.split(" ", 2)[0];
            if (command.equalsIgnoreCase("ping"))
                return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public String usage() {
        return "Returns ping.";
    }

    @Override
    public String getAlias() {
        return "ping";
    }

}
