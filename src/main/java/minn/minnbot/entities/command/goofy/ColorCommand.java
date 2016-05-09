package minn.minnbot.entities.command.goofy;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ColorCommand extends CommandAdapter {

    private final int max = Integer.parseInt("FFFFFF", 16);

    public ColorCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        BufferedImage image = new BufferedImage(114, 114, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        int val = new Random().nextInt(max + 1);
        try {
            if (!event.allArguments.isEmpty())
                val = Math.max(Math.min(Integer.parseInt(event.allArguments, 16), max), 0);
        } catch (NumberFormatException e) {
            event.sendMessage("Color **" + event.allArguments + "** not recognized. Must be a hex value between **0** and **FFFFFF**!");
            return;
        }
        g2d.setColor(new Color(val));
        g2d.fill(new Rectangle());
        try {
            g2d.fill(new Rectangle(image.getWidth(), image.getHeight()));
            File f = new File("image.jpg");
            f.createNewFile();
            ImageIO.write(image, "jpg", f);
            if (!event.isPrivate && !((TextChannel) event.channel).checkPermission(event.jda.getSelfInfo(), Permission.MESSAGE_ATTACH_FILES)) {
                event.sendMessage("I am unable to upload files.");
                return;
            }
            event.channel.sendFileAsync(f, new MessageBuilder().appendString("**__Color:__ " + Integer.toHexString(val).toUpperCase() + "**").build(), null);
        } catch (IOException e) {
            logger.logThrowable(e);
        }
    }

    @Override
    public String getAlias() {
        return "color [<value>]";
    }

    public String example() {
        return "color FE1C2D";
    }

}
