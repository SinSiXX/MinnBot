package minn.minnbot.util;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.User;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MiscUtil {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 2L, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), r -> {
        Thread t = new Thread(r, "AvatarUpdating");
        t.setPriority(1);
        t.setDaemon(true);
        return t;
    });

    public static void getAvatar(User u, Consumer<BufferedImage> callback) throws IOException, UnirestException {
        executor.submit(() -> {
            assert u != null : "User was null!";

            String url = u.getAvatarUrl();
            assert url != null : "Avatar is null!";

            JDA api = u.getJDA();

            InputStream stream = null;
            try {
                stream = Unirest.get(url).header("authorization", api.getAuthToken()).asBinary().getBody();
            } catch (UnirestException e) {
                e.printStackTrace();
                callback.accept(null);
            }
            assert stream != null : "Input stream was null!";
            try {
                callback.accept(ImageIO.read(stream));
            } catch (IOException e) {
                e.printStackTrace();
                callback.accept(null);
            }
        });
    }

}
