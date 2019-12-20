package universe;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import universe.karaoke.Constant;
import universe.karaoke.KaraokeGame;
import universe.karaoke.listener.GuildListener;

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

		// TextChannel channel = this.jda.getTextChannelById("457747767637573644");
		// Message message = channel.sendMessage("Test message.").complete();
		// message.addReaction(Constant.MICROPHONE_EMOTE).complete();

		// this.activeGames = new HashMap<String, KaraokeGame>();
		// this.audioPlayers = new HashMap<String, AudioPlayer>();
		//
		// this.audioManager = new DefaultAudioPlayerManager();
		// AudioSourceManagers.registerLocalSource(this.audioManager);
		//
		// this.jda.addEventListener(new MessageListener(this));
		this.jda.addEventListener(new GuildListener(this));

		// Guild guild = this.jda.getGuildById("457735247724019712");
		//
		// Member member = guild.getMemberById("150506349200015360");
		// member.mute(true).complete();

		// VoiceChannel vc = this.jda.getVoiceChannelById("657356003310043150");
		//
		// AudioManager am = guild.getAudioManager();
		// am.openAudioConnection(vc);
		//
		//
		// this.audioManager = new DefaultAudioPlayerManager();
		// AudioSourceManagers.registerLocalSource(this.audioManager);
		//
		// AudioPlayer player = this.audioManager.createPlayer();

		//
		// am.setSendingHandler(new MP3AudioSource(player));
		//
		// AudioScheduler scheduler = new AudioScheduler();
		// player.addListener(scheduler);
		//
		// File file = new File("C:/Users/Charles/Desktop/FELT Vivienne You're the Shine(Night Butterflies).mp3");
		// this.audioManager.loadItem(file.getAbsolutePath(), new AudioLoader(player));
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
