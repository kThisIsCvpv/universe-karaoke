package universe.karaoke.listener;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import universe.Universe;
import universe.karaoke.KaraokeGame;
import universe.karaoke.audio.AudioStateListener;
import universe.karaoke.audio.AudioWrapper;
import universe.karaoke.util.GameState;
import universe.karaoke.util.MemberTag;

public class MessageListener extends ListenerAdapter {

	private Universe universe;

	public MessageListener(Universe universe) {
		this.universe = universe;
	}

	public Universe getUniverse() {
		return universe;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.isFromType(ChannelType.PRIVATE)) {
			System.out.println(String.format("[PM] %s: %s", event.getAuthor().getName(), event.getMessage().getContentDisplay()));
		} else if (event.isFromType(ChannelType.TEXT)) {
			if (event.getAuthor().isFake())
				return;

			System.out.println(String.format("[%s][%s] %s: %s", event.getGuild().getName(), event.getTextChannel().getName(), event.getMember().getEffectiveName(), event.getMessage().getContentRaw()));

			Guild guild = event.getGuild();
			String ugid = guild.getId();

			Member member = event.getMember();
			TextChannel channel = event.getTextChannel();
			String raw = event.getMessage().getContentRaw();

			if (raw.startsWith("::setup") && this.hasPermission(member)) {
				String msg = raw.substring("::setup".length()).trim();
				String[] args = msg.split(" ");
				if (args.length < 2) {
					channel.sendMessage("Sorry, I don't understand your query syntax.").complete();
					return;
				} else {
					KaraokeGame game = this.universe.getKarokeGame(ugid);
					if (game != null && game.getGameState() != GameState.SETUP) {
						channel.sendMessage("Sorry, there is already a game going on.").complete();
						return;
					}

					game = new KaraokeGame(this.universe);
					this.universe.setKaraokeGame(ugid, game);

					MemberTag targetTag = MemberTag.getMemberTag(args[0]);
					Member target = null;

					if (targetTag != null) {
						target = guild.getMemberById(targetTag.getId());
					}

					if (target == null) {
						channel.sendMessage("Sorry, I could find any members matching that name.").complete();
						return;
					}

					GuildVoiceState gvs = target.getVoiceState();

					if (gvs == null || !gvs.inVoiceChannel()) {
						channel.sendMessage("Sorry, but they are not in a voice channel.").complete();
						return;
					}

					AudioManager am = guild.getAudioManager();
					VoiceChannel targetVoice = gvs.getChannel();

					if (am.isConnected()) {
						VoiceChannel currentVoice = am.getConnectedChannel();

						if (!currentVoice.getId().equals(targetVoice.getId())) {
							am.openAudioConnection(targetVoice);
						}
					} else {
						am.openAudioConnection(targetVoice);
					}

					AudioPlayer player = this.universe.getAudio(ugid);
					if (player != null)
						player.destroy();

					player = this.universe.getAudioManager().createPlayer();
					this.universe.setAudio(ugid, player);

					am.setSendingHandler(new AudioWrapper(player));
					player.addListener(new AudioStateListener(this.universe, game));

					game.setGuild(ugid);
					game.setVoiceChannel(gvs.getChannel().getId());
					game.setTextChannel(channel.getId());
					game.setSinger(target.getId());
					game.setSongLink(args[1]);

					for (int i = 2; i < args.length; i++) {
						if (args[i].equalsIgnoreCase("--no-vocals")) {
							game.setVocals(false);
						}
					}

					channel.sendMessage("Beginning to download: ``" + game.getSongLink() + "``...").complete();
					game.startDownload();
					return;
				}
			} else if ((raw.startsWith("::reset") || raw.startsWith("::kill") || raw.startsWith("::restart")) && this.hasPermission(member)) {
				KaraokeGame game = this.universe.getKarokeGame(ugid);

				if (game == null || game.getGameState() == GameState.SETUP) {
					channel.sendMessage("Sorry, but there's no reason to reset right now.").complete();
					return;
				}

				game.kill();
				this.universe.removeAudio(ugid);
				this.universe.removeKaraokeGame(ugid);

				channel.sendMessage("Success! The karaoke bot has been terminated.").complete();
				return;
			}
		}
	}

	public boolean hasPermission(Member member) {
		if (member == null)
			return false;
		return member.hasPermission(Permission.ADMINISTRATOR);
	}
}
