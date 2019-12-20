package universe.karaoke.util;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Member;

public class MP3AudioSource implements AudioSendHandler {

	private AudioPlayer audioPlayer;

	private ByteBuffer buffer;
	private MutableAudioFrame frame;

	public MP3AudioSource(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;

		this.buffer = ByteBuffer.allocate(1024);
		this.frame = new MutableAudioFrame();
		this.frame.setBuffer(this.buffer);
	}

	public boolean canProvide() {
		return audioPlayer.provide(this.frame);
	}

	public ByteBuffer provide20MsAudio() {
		// System.out.println(lastFrame.getDataLength() + " | " + lastFrame.getData().length);
		// return lastFrame.getData();
		// ((Buffer) this.buffer).flip();
		// return buffer;
		
		this.buffer.flip();
		return buffer;

		// (())
		// lastFrame.getData(this.buffer, 0);
		// return this.buffer;
	}

	public boolean isOpus() {
		return true;
	}
}
