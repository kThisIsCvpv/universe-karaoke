package universe.karaoke;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class App {

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("You must specify the video location.");
			return;
		}

		long startTime = System.currentTimeMillis();
		System.out.println("Downloading the video from YouTube.");

		File baseAudio = downloadYoutubeAudio(args[0]);

		if (baseAudio == null) {
			System.err.println("Unable to download the video from YouTube.");
			return;
		}

		System.out.println("Removing the vocals from the audio.");

		File vocalAudio = removeVocals(baseAudio);

		if (vocalAudio == null) {
			System.err.println("Unable to remove the vocals from the audio.");
			return;
		}

		System.out.println("Success! Elapsed time: " + (System.currentTimeMillis() - startTime) + " ms");
		System.out.println(vocalAudio.getAbsolutePath());
	}

	public static File downloadYoutubeAudio(String url) {
		try {
			ProcessBuilder pb = new ProcessBuilder("youtube-dl", "--extract-audio", "--audio-format", "mp3", url);

			// youtube-dl --extract-audio --prefer-ffmpeg --audio-format mp3 
			
			pb.redirectErrorStream(true);
			Process p = pb.start();
			
			InputStream is = p.getInputStream();
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
				return new File(fileName);

			throw new NullPointerException("Unable to find valid destination.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static File removeVocals(File source) {
		if (source == null || !source.exists())
			throw new NullPointerException("Source file cannot be null!");

		try {
			ProcessBuilder pb = new ProcessBuilder("sox", source.getAbsolutePath(), source.getName() + "-output.mp3", "oops");

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

			if (exitCode == 0) {
				return new File(source.getName() + "-output.mp3");
			}

			throw new NullPointerException("Vocal removal failed. Exit code: " + exitCode);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
}
