package universe.karaoke.util;

public enum GameState {

	/*
	 * 1: A new game begins.
	 * 2: Specify who will be singing, what song they will be singing, and whether or not to remove vocals.
	 * 		2.1: Source audio can be via mp3 or youtube.
	 * 3: The script will download the song.
	 * 4: The script will prompt the singer to ready up.
	 * 5: The singer will ready up. Then song will begin playing.
	 * 		5.1: Everybody will be muted except the bot and singer.
	 * 		5.2: New members joining the channel will also be muted.
	 * 6: The song ends. Everybody will be unmuted. The bot disconnects.
	 */
	
	SETUP, DOWNLOAD, READYUP, LOADING, LIVE;
	
}
