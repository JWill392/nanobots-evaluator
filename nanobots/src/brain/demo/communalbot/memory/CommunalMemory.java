package brain.demo.communalbot.memory;

import entity.bot.Memory;

public abstract class CommunalMemory {
	private static final BotRole[] BOT_ROLE_VALUES = BotRole.values();
	public enum BotRole {
		UNDECIDED, // ordinal is zero, so default
		BREEDER,
		FEEDER,
		FIGHTER;
	}

	private final Memory mem;
	public CommunalMemory(Memory mem) {
		this.mem = mem;
	}

	public static BotRole getRole(Memory mem) {
		return BOT_ROLE_VALUES[mem.getBits(0, 3)];
	}

	protected void setRole(BotRole role) {
		mem.setBits(0, 3, role.ordinal());
	}

	public Memory getData() {
		return (Memory) mem.clone();
	}
}
