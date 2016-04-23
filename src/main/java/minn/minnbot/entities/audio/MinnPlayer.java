package minn.minnbot.entities.audio;

import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.audio.player.FilePlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.List;

public class MinnPlayer extends FilePlayer {

    private List<File> playlist = new LinkedList<>();
    private File previous;
    private File current;
    private boolean debug = false;

    public MinnPlayer() {
        super();
    }

    public File getCurrent() {
        return current;
    }

    public void add(File f) throws UnsupportedAudioFileException, IOException {
        if (!f.isFile() || !f.exists()) {
            throw new UnsupportedAudioFileException();
        }
        if (playlist.isEmpty() && !this.isPlaying()) {
            current = f;
            setAudioFile(current);
            if (!isStarted())
                play();
            else
                restart();
            return;
        }
        playlist.add(f);
    }

    public List<File> getPlaylist() {
        return playlist;
    }

    public void next() {
        playNext();
    }

    public void prev() throws UnexpectedException, IllegalAccessException {
        if (previous == null) {
            throw new UnexpectedException("Nothing played.");
        }
        if (!previous.isFile() || !previous.exists()) {
            throw new IllegalAccessException("File is not available");
        }
    }

    public File getPrevious() {
        return previous;
    }

    private void playNext() { // FIXME
        if (debug)
            System.out.println(TimeUtil.timeStamp() + " [INFO] Playing next...");
        // reset();
        while (!playlist.isEmpty()) {
            try {
                // Try to pick first file
                setAudioFile(playlist.get(0));
                super.play();
                // Catch exception
            } catch (IOException | UnsupportedAudioFileException e) {
                if (debug)
                    System.err.println(TimeUtil.timeStamp() + " [ERROR] " + e.getMessage());
                playlist.remove(0);

                // Continue because the file was unusable
                continue;
            }
            // Break loop because a file was usable
            break;
        }
        if (playlist.isEmpty()) {
            System.out.println(TimeUtil.timeStamp() + " [INFO] Playlist ended.");
            super.stop();
            return;
        }
        try {
            //noinspection ResultOfMethodCallIgnored
            previous.delete();
        } catch (Exception ignored) {
        }
        previous = current;
        current = playlist.get(0);
        playlist.remove(0);
        if (debug) {
            System.out.println(TimeUtil.timeStamp() + " [INFO] Selected: " + current.getName());
            System.out.println(TimeUtil.timeStamp() + " [INFO] Now playing selected file.");
        }
    }

    @Override
    public void stop() {
        if (debug) {
            new Exception().printStackTrace();
            System.out.println(TimeUtil.timeStamp() + " [INFO] Playback stopped.");
        }
        playNext();
    }

}
