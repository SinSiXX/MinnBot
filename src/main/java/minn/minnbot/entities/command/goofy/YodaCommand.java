package minn.minnbot.entities.command.goofy;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

import java.net.URLEncoder;
import java.util.List;

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
    public boolean isCommand(String message, List<String> prefixList) {
        String[] p = message.split(" ", 2);
        if(p.length < 1)
            return false;
        if(p[0].equalsIgnoreCase(prefix + "yoda"))
            return true;
        for(String fix : prefixList) {
            if(p[0].equalsIgnoreCase(fix + "yoda"))
                return true;
        }
        return false;
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
