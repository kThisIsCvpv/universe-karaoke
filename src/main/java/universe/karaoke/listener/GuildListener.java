package universe.karaoke.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import universe.Universe;
import universe.karaoke.Constant;
import universe.karaoke.KaraokeGame;
import universe.karaoke.util.GameState;

public class GuildListener extends ListenerAdapter {

	private Universe universe;

	public GuildListener(Universe universe) {
		this.universe = universe;
	}

	public Universe getUniverse() {
		return universe;
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		Guild guild = event.getGuild();
		VoiceChannel to = event.getChannelJoined();
		Member member = event.getMember();

		System.out.printf("[GuildVoiceJoinEvent - %s] %s has joined %s.\n", guild.getName(), member.getEffectiveName(), to.getName());

		String ugid = guild.getId();
		KaraokeGame game = this.universe.getKarokeGame(ugid);
		if (game == null || game.getGameState() != GameState.LIVE)
			return;

		if (game.getVoiceChannel() == null || !to.getId().equals(game.getVoiceChannel()))
			return;

		if (game.shouldMute(member))
			game.mute(guild, member);
	}

	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
		Guild guild = event.getGuild();
		VoiceChannel from = event.getChannelLeft();
		VoiceChannel to = event.getChannelJoined();
		Member member = event.getMember();

		System.out.printf("[GuildVoiceMoveEvent - %s] %s moved from %s to %s.\n", guild.getName(), member.getEffectiveName(), from.getName(), to.getName());

		String ugid = guild.getId();
		KaraokeGame game = this.universe.getKarokeGame(ugid);
		if (game == null || game.getGameState() != GameState.LIVE)
			return;

		if (game.getVoiceChannel() == null || !to.getId().equals(game.getVoiceChannel()))
			return;

		if (game.shouldMute(member))
			game.mute(guild, member);
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		Guild guild = event.getGuild();
		VoiceChannel from = event.getChannelLeft();
		Member member = event.getMember();

		System.out.printf("[GuildVoiceLeaveEvent - %s] %s has left %s.\n", guild.getName(), member.getEffectiveName(), from.getName());
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (!event.isFromGuild())
			return;

		Guild guild = event.getGuild();
		String ugid = guild.getId();

		Member member = event.getMember();
		ReactionEmote emote = event.getReactionEmote();

		String mid = event.getMessageId();
		TextChannel channel = event.getTextChannel();

		System.out.printf("[MessageReactionAddEvent - %s] %s added emote %s to %s in %s.\n", guild.getName(), member.getEffectiveName(), emote.getName(), mid, channel.getName());

		KaraokeGame game = this.universe.getKarokeGame(ugid);
		if (game == null || game.getGameState() != GameState.READYUP)
			return;

		if (!game.getReadyMessage().equals(mid))
			return;

		if (!emote.getName().equals(Constant.MICROPHONE_EMOTE))
			return;

		if (!member.getId().equals(game.getSinger()))
			return;

		game.startSong();
	}
}
