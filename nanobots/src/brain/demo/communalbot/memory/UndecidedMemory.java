package brain.demo.communalbot.memory;

import entity.bot.Memory;

public class UndecidedMemory extends CommunalMemory {
	public UndecidedMemory(Memory mem) {
		super(mem);
		setRole(BotRole.UNDECIDED);
	}

	public void decide(BotRole role) {
		setRole(role);
	}
}
