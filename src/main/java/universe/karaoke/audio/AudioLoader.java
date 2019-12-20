package universe.karaoke.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import universe.Universe;
import universe.karaoke.KaraokeGame;
import universe.karaoke.util.GameState;

public class AudioLoader implements AudioLoadResultHandler {

	private Universe universe;
	private KaraokeGame game;

	private AudioPlayer player;

	public AudioLoader(Universe universe, KaraokeGame game, AudioPlayer player) {
		this.universe = universe;
		this.game = game;
		this.player = player;
	}

	public void trackLoaded(AudioTrack track) {
		System.out.println("[AudioLoader] Single source found!");
		this.player.playTrack(track);

		JDA jda = this.universe.getJDA();

		Guild guild = jda.getGuildById(this.game.getGuild());
		VoiceChannel voice = jda.getVoiceChannelById(this.game.getVoiceChannel());
		TextChannel text = this.getChannel();

		text.sendMessage("**Now Playing:** " + game.getSongName()).queue();

		for (Member member : voice.getMembers()) {
			if (this.game.shouldMute(member)) {
				this.game.mute(guild, member);
			}
		}
	}

	public void playlistLoaded(AudioPlaylist playlist) {
		System.out.println("[AudioLoader] Playlist found!");
	}

	public void noMatches() {
		System.err.println("[AudioLoader] Unable to find any matches.");

		TextChannel channel = this.getChannel();
		channel.sendMessage("Sorry, we were unable to find the requested file.").complete();
		this.game.setGameState(GameState.SETUP);
	}

	public void loadFailed(FriendlyException exception) {
		System.err.println("[AudioLoader] Unable to load music from source.");

		TextChannel channel = this.getChannel();
		channel.sendMessage("Sorry, we were unable to load the requested file.").complete();
		this.game.setGameState(GameState.SETUP);
	}

	public TextChannel getChannel() {
		JDA jda = this.universe.getJDA();
		TextChannel channel = jda.getTextChannelById(this.game.getTextChannel());
		return channel;
	}
}
