package brain.demo.communalbot.memory;

import entity.bot.Memory;

public class BreederMemory extends CommunalMemory {
	public BreederMemory(Memory mem) {
		super(mem);
		setRole(BotRole.BREEDER);
	}

}
