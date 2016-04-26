package minn.minnbot.util;

import minn.minnbot.MinnBot;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.function.Consumer;

public class EvalUtil {
    private static ScriptEngine engine;

    public static void init() throws ScriptException {
        engine = new ScriptEngineManager().getEngineByName("Nashorn");
    }

    public static void eval(String input, net.dv8tion.jda.events.message.MessageReceivedEvent event, MinnBot bot, Consumer<String> callback) {
        Object out;
        try {
            engine.put("event", event);
            engine.put("channel", event.getChannel());
            engine.put("guild", event.getGuild());
            engine.put("me", event.getAuthor());
            engine.put("api", event.getJDA());
            engine.put("bot", bot);
        } catch (NullPointerException e) {
        }
        int method = 0;
        try {
            engine.eval("var imports = new JavaImporter(java.io, java.lang, java.util);");
            engine.eval("function log(String) { return String }");
            if (input.endsWith("\n```")) {
                method = 1;
                out = engine.eval("(function(){ with(imports) { "
                        + input.substring("```js\n".length(), input.length() - "\n```".length()) + "} })();");

            } else if (input.endsWith("```")) {
                method = 2;
                out = engine.eval("(function(){ with(imports) { "
                        + input.substring("```js\n".length(), input.length() - "```".length()) + "} })();");

            } else if (input.startsWith("`") && input.endsWith("`")) {
                method = 3;
                out = engine.eval("(function(){ with(imports) { "
                        + input.substring("`".length(), input.length() - "`".length()) + "\n} })();");
            } else {
                method = 4;
                out = engine.eval("(function(){ with(imports) { " + input + "\n} })();");
            }
        } catch (Exception e) {
            out = e.getMessage();
        }
        if (out != null) {
            out = out.toString().replace("@everyone", "(mass mention)").replace("`", "\u0001`\u0001");
        }
        switch (method) {
            default: {
                out = ("**__Input:__** ```js\n>" + input + "```\n" + out
                        + "\nUsage:\n\\`\\`\\`js\nreturn result\n\\`\\`\\`");
                break;
            }
            case 1: {
                out = ("**__Input:__** ```js\n>"
                        + input.substring("```js\n".length(), input.length() - 3) + "```\n**__Output:__** "
                        + ((out == null) ? "`Executed without erorrs!`" : "`" + out + "`"));
                break;
            }
            case 2: {
                out = ("**__Input:__** ```js\n>"
                        + input.substring("```js\n".length(), input.length() - 4) + "```\n**__Output:__** "
                        + ((out == null) ? "`Executed without erorrs!`" : "`" + out + "`"));
                break;
            }
            case 3: {
                out = ("**__Input:__** ```js\n>" + input.substring(1, input.length() - 1)
                        + "```\n**__Output:__** "
                        + ((out == null) ? "`Executed without erorrs!`" : "`" + out + "`"));
                break;
            }
            case 4: {
                out = ("**__Input:__** ```js\n>" + input + "```\n**__Output:__** "
                        + ((out == null) ? "`Executed without erorrs!`" : "`" + out + "`"));
                break;
            }
        }
        callback.accept(out.toString());
    }
}
