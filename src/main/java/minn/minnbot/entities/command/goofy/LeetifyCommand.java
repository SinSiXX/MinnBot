package minn.minnbot.entities.command.goofy;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

public class LeetifyCommand extends CommandAdapter {

    public LeetifyCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) event.sendMessage(leetify("No input given!"));
        else event.sendMessage(leetify(event.allArguments));
    }

    @Override
    public String getAlias() {
        return "1337 <text>";
    }

    public String example() {
        return "1337 leet";
    }


    public static String leetify(String input) {
        return input
                .replaceAll("[OoDd]", "0")
                .replaceAll("[Ii1JjLl]", "1")
                .replaceAll("[Zz]", "2")
                .replaceAll("[Ee]", "3")
                .replaceAll("[Aa]", "4")
                .replaceAll("[Ss]", "5")
                .replaceAll("[G]", "6")
                .replaceAll("[Tt]", "7")
                .replaceAll("[Bb]", "8")
                .replaceAll("[gpP]", "9");
    }
    
}
