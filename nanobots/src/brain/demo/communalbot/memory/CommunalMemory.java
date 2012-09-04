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

	protected final Memory mem;
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

	public static CommunalMemory getMemory(Memory mem) {
		switch (getRole(mem)) {
		case UNDECIDED:
			return new UndecidedMemory(mem);
		case BREEDER:
			return new BreederMemory(mem);
		case FEEDER:
			return new FeederMemory(mem);
		case FIGHTER:
			return new FighterMemory(mem);
		default:
			throw new IllegalStateException();
		}
	}

	public static CommunalMemory getBlankMemory(BotRole role) {
		switch (role) {
		case UNDECIDED:
			return new UndecidedMemory(new Memory());
		case BREEDER:
			return new BreederMemory(new Memory());
		case FEEDER:
			return new FeederMemory(new Memory());
		case FIGHTER:
			return new FighterMemory(new Memory());
		default:
			throw new IllegalStateException();
		}
	}

	public BotRole getRole() {
		return getRole(mem);
	}
}
