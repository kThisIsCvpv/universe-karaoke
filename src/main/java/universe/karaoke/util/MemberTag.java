package universe.karaoke.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemberTag {

	private static final Pattern[] PATTERNS = new Pattern[] { Pattern.compile("<@!([0-9]+)>"), Pattern.compile("<@([0-9]+)>") };

	private String id;

	private MemberTag(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public long getIdLong() {
		return Long.parseLong(this.id);
	}

	public static MemberTag getMemberTag(String input) {
		if (input == null || input.isEmpty())
			return null;

		try {
			String l = Long.toString(Long.parseLong(input.trim()));
			return new MemberTag(l);
		} catch (Exception ex) {}

		for (Pattern pattern : PATTERNS) {
			Matcher mA = pattern.matcher(input);
			if (mA.matches()) {
				String id = mA.group(1);
				return new MemberTag(id);
			}
		}

		return null;
	}
}
