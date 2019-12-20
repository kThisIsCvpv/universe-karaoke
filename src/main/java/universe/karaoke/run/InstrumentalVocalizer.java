package universe.karaoke.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import universe.karaoke.KaraokeGame;
import universe.karaoke.util.GameState;

public class InstrumentalVocalizer implements Runnable, Actionable {

	private KaraokeGame game;

	private Process process;

	private boolean done;
	private boolean killed;

	public InstrumentalVocalizer(KaraokeGame game) {
		this.game = game;

		this.done = false;
		this.killed = false;
	}

	public void start() {
		new Thread(this).start();
	}

	public void run() {
		String id = this.game.getTextChannel();
		JDA jda = this.game.getUniverse().getJDA();
		TextChannel channel = jda.getTextChannelById(id);

		File sourceFile = new File(this.game.getSongPath());
		if (sourceFile == null || !sourceFile.exists()) {
			channel.sendMessage("Sorry, unable to find source file to vocalize.").complete();
			this.game.setGameState(GameState.SETUP);
			return;
		}

		File outputFile = null;
		long startTime = System.currentTimeMillis();

		try {
			ProcessBuilder pb = new ProcessBuilder("sox", sourceFile.getAbsolutePath(), sourceFile.getName() + "-output.mp3", "oops");

			pb.redirectErrorStream(true);
			Process p = pb.start();

			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String ln;
			while ((ln = br.readLine()) != null) {
				if (!ln.isEmpty()) {
					System.out.println("\t" + ln);
				}
			}

			int exitCode = p.waitFor();

			if (exitCode == 0)
				outputFile = new File(sourceFile.getName() + "-output.mp3");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (!this.killed) {
			long elapsedTime = System.currentTimeMillis() - startTime;

			if (channel != null) {
				if (outputFile == null) {
					channel.sendMessage("Sorry, unable to vocalize this video. Elapsed time: " + elapsedTime + " ms.").complete();
					this.game.setGameState(GameState.SETUP);
				} else {
					channel.sendMessage("Success! The soundtrack has been vocalized! Elapsed time: " + elapsedTime + " ms.").complete();
					this.game.setSongPath(outputFile.getAbsolutePath());
					this.game.finishProcessing();
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
