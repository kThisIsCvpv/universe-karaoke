package universe.karaoke.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import universe.Universe;
import universe.karaoke.KaraokeGame;
import universe.karaoke.util.GameState;

public class AudioStateListener extends AudioEventAdapter {

	private Universe universe;
	private KaraokeGame game;

	public AudioStateListener(Universe universe, KaraokeGame game) {
		this.universe = universe;
		this.game = game;
	}

	@Override
	public void onPlayerPause(AudioPlayer player) {
		// Player was paused
		System.out.println("[AudioStateListener] onPlayerPause");
	}

	@Override
	public void onPlayerResume(AudioPlayer player) {
		// Player was resumed
		System.out.println("[AudioStateListener] onPlayerResume");
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		// A track started playing
		System.out.println("[AudioStateListener] onTrackStart");

		if (this.game.getGameState() == GameState.LOADING)
			this.game.setGameState(GameState.LIVE);
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		System.out.println("[AudioStateListener] onTrackEnd");

		if (this.game.getGameState() != GameState.LIVE)
			return;

		JDA jda = this.universe.getJDA();
		TextChannel channel = jda.getTextChannelById(this.game.getTextChannel());
		channel.sendMessage("The audio track has finished.").complete();

		this.game.setGameState(GameState.SETUP);

		if (endReason.mayStartNext) {
			// Start next track
		}

		// endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
		// endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
		// endReason == STOPPED: The player was stopped.
		// endReason == REPLACED: Another track started playing while this had not finished
		// endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
		// clone of this back to your queue
	}

	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		// An already playing track threw an exception (track end event will still be received separately)
		System.out.println("[AudioStateListener] onTrackException");
	}

	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		// Audio track has been unable to provide us any audio, might want to just start a new track
		System.out.println("Event Triggered > Track Stuck");
	}
}
