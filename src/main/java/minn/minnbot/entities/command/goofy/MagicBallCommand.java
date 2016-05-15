package minn.minnbot.entities.command.goofy;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

import java.util.Random;

public class MagicBallCommand extends CommandAdapter {

    private String[] yes = {"Yea sure", "Yes", "Why not", "I guess", "Confirmed", "Affirmative", "Yes actually", "I can't believe I'm saying this but yes", "Always", "Of course", "Am I a bot?? YES"};
    private String[] no = {"Never", "nah", "No way", "NONONONONONO", "Unlikely", "I'd say no", "Not that I know", "I don't think so", "Nope", "No", "NEIN"};
    private String[] unsure = {"Ask me again later", "I don't know fam", "Google it", "Ask b1nzy, he is API support!"};
    private String[][] arrays = {yes, no, unsure};

    public MagicBallCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        Random rng = new Random();
        int col = rng.nextInt(arrays.length);
        int row = rng.nextInt(arrays[col].length);
        event.sendMessage(String.format("**%s, %s**", event.author.getAsMention(), arrays[col][row]));
    }

    @Override
    public String getAlias() {
        return "8ball <question>";
    }

    public String example() {
        return "8ball Does this command work?";
    }
}
