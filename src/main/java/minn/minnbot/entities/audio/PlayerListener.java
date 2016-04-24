package minn.minnbot.entities.audio;

import minn.minnbot.manager.PlayingFieldManager;
import net.dv8tion.jda.player.hooks.PlayerListenerAdapter;
import net.dv8tion.jda.player.hooks.events.*;

public class PlayerListener extends PlayerListenerAdapter {

    public PlayerListener() {}

    @Override
    public void onNext(NextEvent event) {
        String name = event.getPlayer().getPreviousAudioSource().getInfo().getTitle();
        PlayingFieldManager.removeGame(name);
    }

    @Override
    public void onFinish(FinishEvent event) {
        String name = event.getPlayer().getPreviousAudioSource().getInfo().getTitle();
        PlayingFieldManager.removeGame(name);
    }

    @Override
    public void onPlay(PlayEvent event) {
        String name = event.getPlayer().getCurrentAudioSource().getInfo().getTitle();
        PlayingFieldManager.addGame(name);
    }

    @Override
    public void onStop(StopEvent event) {
        String name = event.getPlayer().getPreviousAudioSource().getInfo().getTitle();
        PlayingFieldManager.removeGame(name);
    }

    @Override
    public void onSkip(SkipEvent event) {
        String name = event.getPlayer().getPreviousAudioSource().getInfo().getTitle();
        PlayingFieldManager.removeGame(name);
    }

}
