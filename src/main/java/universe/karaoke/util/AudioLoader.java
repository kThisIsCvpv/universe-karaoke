package universe.karaoke.util;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

public class AudioLoader implements AudioLoadResultHandler {

	private AudioPlayer player;

	public AudioLoader(AudioPlayer player) {
		this.player = player;
	}

	public void trackLoaded(AudioTrack track) {
		System.out.println("Single source found!");
				
		AudioTrackInfo info = track.getInfo();
		System.out.println("> ID: " + track.getIdentifier());
		System.out.println("> Title: " + info.title);
		System.out.println("> Author: " + info.author);
		System.out.println("> Identifier: " + info.identifier);

		this.player.playTrack(track);
	}

	public void playlistLoaded(AudioPlaylist playlist) {
		System.out.println("Playlist found!");
		for (AudioTrack track : playlist.getTracks()) {
			System.out.println("> " + track.getInfo().title);
		}
	}

	public void noMatches() {
		System.err.println("Unable to find any matches.");
	}

	public void loadFailed(FriendlyException exception) {
		System.err.println("Unable to load music from source.");
	}

}
