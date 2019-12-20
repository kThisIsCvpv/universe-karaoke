package universe.karaoke;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import universe.Universe;
import universe.karaoke.audio.AudioLoader;
import universe.karaoke.run.Actionable;
import universe.karaoke.run.InstrumentalVocalizer;
import universe.karaoke.run.YoutubeDownloader;
import universe.karaoke.util.GameState;

public class KaraokeGame {

	private Universe universe;

	private GameState gameState;

	private String guild;
	private String textChannel;
	private String voiceChannel;

	private String singer;

	private boolean vocals = true;

	private String songLink;
	private String songName;
	private String songPath;

	private String readyMessage;

	private Actionable process;

	public KaraokeGame(Universe universe) {
		this.universe = universe;

		this.gameState = GameState.SETUP;
	}

	public Universe getUniverse() {
		return this.universe;
	}

	public GameState getGameState() {
		return this.gameState;
	}

	public void setGameState(GameState state) {
		this.gameState = state;
	}

	public String getGuild() {
		return this.guild;
	}

	public void setGuild(String guild) {
		this.guild = guild;
	}

	public String getTextChannel() {
		return this.textChannel;
	}

	public void setTextChannel(String channel) {
		this.textChannel = channel;
	}

	public String getVoiceChannel() {
		return this.voiceChannel;
	}

	public void setVoiceChannel(String channel) {
		this.voiceChannel = channel;
	}

	public String getSinger() {
		return this.singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getReadyMessage() {
		return this.readyMessage;
	}

	public void setReadyMessage(String msg) {
		this.readyMessage = msg;
	}

	public boolean getVocals() {
		return this.vocals;
	}

	public void setVocals(boolean bool) {
		this.vocals = bool;
	}

	public String getSongLink() {
		return this.songLink;
	}

	public void setSongLink(String url) {
		this.songLink = url;
	}

	public String getSongName() {
		return this.songName;
	}

	public void setSongName(String name) {
		this.songName = name;
	}

	public String getSongPath() {
		return this.songPath;
	}

	public void setSongPath(String path) {
		this.songPath = path;
	}

	public Actionable getProcess() {
		return this.process;
	}

	public void setProcess(Actionable process) {
		this.process = process;
	}

	public void startDownload() {
		if (this.gameState != GameState.SETUP)
			return;

		this.gameState = GameState.DOWNLOAD;

		YoutubeDownloader yt = new YoutubeDownloader(this);
		yt.start();
		this.setProcess(yt);
	}

	public void startRender() {
		if (this.gameState != GameState.DOWNLOAD)
			return;

		InstrumentalVocalizer iv = new InstrumentalVocalizer(this);
		iv.start();
		this.setProcess(iv);
	}

	public void finishProcessing() {
		if (this.gameState != GameState.DOWNLOAD)
			return;

		this.setProcess(null);

		JDA jda = this.universe.getJDA();
		TextChannel channel = jda.getTextChannelById(this.textChannel);

		Message message = channel.sendMessage("Hey, <@" + this.singer + ">. Press the microphone emote when you're ready to go.").complete();
		message.addReaction(Constant.MICROPHONE_EMOTE).complete();
		this.readyMessage = message.getId();

		this.gameState = GameState.READYUP;

	}

	public void startSong() {
		if (this.gameState != GameState.READYUP)
			return;

		AudioPlayer player = this.universe.getAudio(this.guild);
		this.universe.getAudioManager().loadItem(this.songPath, new AudioLoader(this.universe, this, player));

		this.gameState = GameState.LOADING;
	}

	public void kill() {
		if (this.process != null)
			this.process.kill();

		Guild guild = this.universe.getJDA().getGuildById(this.guild);
		AudioManager am = guild.getAudioManager();

		if (am.isConnected()) {
			am.closeAudioConnection();
		}

		if (this.gameState == GameState.LOADING || this.gameState == GameState.LIVE) {
			AudioPlayer player = this.universe.getAudio(this.guild);
			player.destroy();
		}

		this.gameState = GameState.SETUP;
	}
}
