package minn.minnbot.util;

import java.util.List;

public class CommandUtil {

    public static boolean isCommand(String prefix, String alias, String message) {
        return message.matches("((?i)\\Q" + prefix + "\\E\\s*\\Q" + alias + "\\E)(\\s+.*|\\s*)");
    }

    public static boolean isCommand(List<String> prefixList, String alias, String message) {
        for (String fix : prefixList) {
            if (isCommand(fix, alias, message))
                return true;
        }
        return false;
    }

}
