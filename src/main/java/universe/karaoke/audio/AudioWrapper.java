package universe.karaoke.audio;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioWrapper implements AudioSendHandler {

	private AudioPlayer audioPlayer;

	private ByteBuffer buffer;
	private MutableAudioFrame frame;

	public AudioWrapper(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;

		this.buffer = ByteBuffer.allocate(1024);
		this.frame = new MutableAudioFrame();
		this.frame.setBuffer(this.buffer);
	}

	public boolean canProvide() {
		return audioPlayer.provide(this.frame);
	}

	public ByteBuffer provide20MsAudio() {
		this.buffer.flip();
		return buffer;
	}

	public boolean isOpus() {
		return true;
	}
}
