package minn.minnbot.entities.command;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

import java.net.URLEncoder;
import java.util.List;

public class QRCodeCommand extends CommandAdapter{

    public QRCodeCommand(String prefix, Logger logger) {
        this.logger = logger;
        this.prefix = prefix;
    }

    @Override
    public void onCommand(CommandEvent event) {
        if(event.allArguments.isEmpty()) {
            event.sendMessage(usage());
            return;
        }
        String method = event.arguments[0];
        if(!method.equalsIgnoreCase("text") && !method.equalsIgnoreCase("url")) {
            event.sendMessage("Invalid method!" + usage());
            return;
        }
        if(!(event.allArguments.length() > event.arguments[0].length() + 1)) {
            event.sendMessage("Invalid input!" + usage());
            return;
        }
        String input = URLEncoder.encode(event.allArguments.substring(method.length() + 1));
        try {
            HttpResponse<String> response = Unirest.get("https://pierre2106j-qrcode.p.mashape.com/api?backcolor=ffffff&ecl=H&forecolor=000000&pixel=3+to+4&text=" + input + "&type=" + method)
                    .header("X-Mashape-Key", "IlX3p3hnDRmsheyTT7z87aT1mrs9p1Qb4WkjsnGUnXKitYqhtf")
                    .header("Accept", "text/plain")
                    .asString();
            event.sendMessage(response.getBody());
        } catch (UnirestException e) {
            event.sendMessage("Something is wrong with my connection try again later.");
        }
    }

    @Override
    public boolean isCommand(String message, List<String> prefixList) {
        String[] p = message.split(" ", 2);
        if(p.length < 1)
            return false;
        if(p[0].equalsIgnoreCase(prefix + "qr"))
            return true;
        for(String fix : prefixList) {
            if(p[0].equalsIgnoreCase(fix + "qr"))
                return true;
        }
        return false;
    }

    public String usage() {
        return "\n**__Methods:__** `text`, `url`\n**__Examples:__** `QR text test`, `QR url http://discordapp.com`";
    }

    @Override
    public String getAlias() {
        return "QR <method> <input>";
    }

    @Override
    public String example() {
        return "QR text This is an example";
    }
}
