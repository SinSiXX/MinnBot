package minn.minnbot.entities.command.goofy;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

import java.net.URLEncoder;

public class YodaCommand extends CommandAdapter{

    public YodaCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            event.sendMessage(Unirest.get("https://yoda.p.mashape.com/yoda?sentence=" + URLEncoder.encode(event.allArguments))
                    .header("X-Mashape-Key", "IlX3p3hnDRmsheyTT7z87aT1mrs9p1Qb4WkjsnGUnXKitYqhtf")
                    .header("Accept", "text/plain")
                    .asString().getBody());
        } catch (UnirestException e) {
            event.sendMessage("Something is wrong with my connection. Try again later.");
        }
    }

    @Override
    public boolean isCommand(String message) {
        String[] parts = message.split(" ",2);
        return parts.length > 0 && parts[0].equalsIgnoreCase(prefix + "yoda");
    }

    @Override
    public String getAlias() {
        return "yoda <sentence>";
    }

    @Override
    public String example() {
        return "yoda This is an example sentence.";
    }

}
