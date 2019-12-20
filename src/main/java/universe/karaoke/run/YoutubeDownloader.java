package universe.karaoke.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import universe.karaoke.KaraokeGame;
import universe.karaoke.util.GameState;

public class YoutubeDownloader implements Runnable, Actionable {

	private KaraokeGame game;

	private Process process;

	private boolean done;
	private boolean killed;

	public YoutubeDownloader(KaraokeGame game) {
		this.game = game;

		this.done = false;
		this.killed = false;
	}

	public void start() {
		new Thread(this).start();
	}

	public void run() {
		File outputFile = null;
		long startTime = System.currentTimeMillis();

		try {
			ProcessBuilder pb = new ProcessBuilder("youtube-dl", "--extract-audio", "--audio-format", "mp3", this.game.getSongLink());

			pb.redirectErrorStream(true);

			this.process = pb.start();

			InputStream is = this.process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String fileName = null;

			String ln;
			while ((ln = br.readLine()) != null) {
				if (!ln.isEmpty()) {
					System.out.println("\t" + ln);
					if (ln.contains("Destination: ")) {
						String name = ln.split("Destination: ")[1];
						if (name.endsWith(".mp3"))
							fileName = name;
					}
				}
			}

			if (fileName != null)
				outputFile = new File(fileName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (!this.killed) {
			long elapsedTime = System.currentTimeMillis() - startTime;

			String id = this.game.getTextChannel();
			JDA jda = this.game.getUniverse().getJDA();
			TextChannel channel = jda.getTextChannelById(id);

			if (channel != null) {
				if (outputFile == null) {
					channel.sendMessage("Sorry, unable to download this video' audio. Elapsed time: " + elapsedTime + " ms.").complete();
					this.game.setGameState(GameState.SETUP);
				} else {
					channel.sendMessage("Success! The video's audio has been downloaded! Elapsed time: " + elapsedTime + " ms.").complete();

					this.game.setSongPath(outputFile.getAbsolutePath());

					String fileName = outputFile.getName();
					int index = fileName.contains(".") ? fileName.lastIndexOf(".") : fileName.length();
					this.game.setSongName(fileName.substring(0, index));

					if (game.getVocals()) {
						channel.sendMessage("Beginning to render soundtrack: ``" + this.game.getSongName() + "``...").complete();
						this.game.startRender();
					} else {
						this.game.finishProcessing();
					}
				}
			}
		}

		this.done = true;
		this.killed = true;
	}

	public boolean isDone() {
		return this.done;
	}

	public void kill() {
		this.killed = true;

		if (!this.done) {
			try {
				if (this.process != null && this.process.isAlive()) {
					this.process.destroyForcibly();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
