package universe.karaoke;

import java.io.File;
import java.nio.file.Files;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import universe.karaoke.util.AudioLoader;
import universe.karaoke.util.AudioScheduler;
import universe.karaoke.util.MP3AudioSource;

public class Universe {

	private JDA jda;

	private AudioPlayerManager audioManager;

	public Universe(final String privateKey) {
		try {
			this.jda = new JDABuilder(privateKey).build();
			this.jda.awaitReady();
		} catch (Exception e) {
			System.err.println("Invalid login credentials.");
			System.exit(1);
			return;
		}

		Guild guild = this.jda.getGuildById("457735247724019712");
		
		Member member = guild.getMemberById("150506349200015360");
		member.mute(true).complete();
		
//		VoiceChannel vc = this.jda.getVoiceChannelById("657356003310043150");
//
//		AudioManager am = guild.getAudioManager();
//		am.openAudioConnection(vc);
//
//		this.audioManager = new DefaultAudioPlayerManager();
//		AudioSourceManagers.registerLocalSource(this.audioManager);
//
//		AudioPlayer player = this.audioManager.createPlayer();
//
//		am.setSendingHandler(new MP3AudioSource(player));
//
//		AudioScheduler scheduler = new AudioScheduler();
//		player.addListener(scheduler);
//
//		File file = new File("C:/Users/Charles/Desktop/FELT Vivienne You're the Shine(Night Butterflies).mp3");
//		this.audioManager.loadItem(file.getAbsolutePath(), new AudioLoader(player));
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
