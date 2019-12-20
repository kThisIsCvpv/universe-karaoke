package universe;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import universe.karaoke.Constant;
import universe.karaoke.KaraokeGame;
import universe.karaoke.listener.GuildListener;
import universe.karaoke.listener.MessageListener;

public class Universe {

	private JDA jda;

	private AudioPlayerManager audioManager;

	private Map<String, AudioPlayer> audioPlayers;
	private Map<String, KaraokeGame> activeGames;

	public Universe(final String privateKey) {
		try {
			this.jda = new JDABuilder(privateKey).build();
			this.jda.awaitReady();
		} catch (Exception e) {
			System.err.println("Invalid login credentials.");
			System.exit(1);
			return;
		}

		this.activeGames = new HashMap<String, KaraokeGame>();
		this.audioPlayers = new HashMap<String, AudioPlayer>();

		this.audioManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerLocalSource(this.audioManager);

		this.jda.addEventListener(new MessageListener(this));
		this.jda.addEventListener(new GuildListener(this));
	}

	public AudioPlayerManager getAudioManager() {
		return this.audioManager;
	}

	public KaraokeGame getKarokeGame(String guild) {
		if (this.activeGames.containsKey(guild))
			return this.activeGames.get(guild);
		return null;
	}

	public void removeKaraokeGame(String guild) {
		this.activeGames.remove(guild);
	}

	public void setKaraokeGame(String guild, KaraokeGame game) {
		this.activeGames.put(guild, game);
	}

	public AudioPlayer getAudio(String guild) {
		if (this.audioPlayers.containsKey(guild))
			return this.audioPlayers.get(guild);
		return null;
	}

	public void removeAudio(String guild) {
		this.audioPlayers.remove(guild);
	}

	public void setAudio(String guild, AudioPlayer game) {
		this.audioPlayers.put(guild, game);
	}

	public JDA getJDA() {
		return this.jda;
	}

	public static void main(String[] args) throws Exception {
		File privateFile = new File(Constant.DISCORD_KEY);

		if (!privateFile.exists()) {
			System.err.println("Missing discord key file: " + Constant.DISCORD_KEY);
			System.exit(1);
			return;
		}

		String privateKey = new String(Files.readAllBytes(privateFile.toPath())).trim();
		new Universe(privateKey);
	}

}
