package minn.minnbot.util;

import java.util.List;

public class CommandUtil {

    public static boolean isCommand(String prefix, String alias, String message) {
        String[] p;
        if (message.contains(" "))
            p = message.split(" ", 2);
        else
            p = new String[]{message};
        return p.length > 0 && p[0].equalsIgnoreCase(prefix + alias);
    }

    public static boolean isCommand(List<String> prefixList, String alias, String message) {
        String[] p = message.split(" ", 2);
        if (p.length <= 0)
            return false;
        for (String fix : prefixList) {
            if (isCommand(fix, alias, p[0]))
                return true;
        }
        return false;
    }

}
