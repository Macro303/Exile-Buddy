package macro.buddy.data;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created by Macro303 on 2019-Dec-02.
 */
public class QuestInfo {
	@NotNull
	private final String name;
	@NotNull
	private final Map<Tags.ClassTag, List<GemInfo>> rewards;

	public QuestInfo(@NotNull String name, @NotNull Map<Tags.ClassTag, List<GemInfo>> rewards) {
		this.name = name;
		this.rewards = rewards;
	}

	@NotNull
	public String getName() {
		return name;
	}

	@NotNull
	public Map<Tags.ClassTag, List<GemInfo>> getRewards() {
		return rewards;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof QuestInfo)) return false;

		QuestInfo questInfo = (QuestInfo) o;

		if (!name.equals(questInfo.name)) return false;
		return rewards.equals(questInfo.rewards);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + rewards.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "QuestInfo{" +
				"name='" + name + '\'' +
				", rewards=" + rewards +
				'}';
	}
}